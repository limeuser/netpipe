package mjoys.netpipe.pipe;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import mjoys.io.ByteBufferOutputStream;
import mjoys.io.Serializer;
import mjoys.util.Address;
import mjoys.util.ByteUnit;
import mjoys.util.ClassUtil;
import mjoys.util.Logger;

public class TcpOutPipe<E> implements OutPipe<E> {
    private String name;
    private PipeStatus status;
    private Serializer serializer;
    
    private Thread sendThread;
    private Thread listenThread;
    
    private AbstractQueue<E> dataQueue;
    private ServerSocket boundSocket;
    private InetSocketAddress boundAddress;
    private List<Connection> connections;
    
    
    private final int capacity = 4 * ByteUnit.KB;
    private final ByteBuffer outBuffer = ByteBuffer.allocate(capacity);
    
    private final Logger logger = new Logger().addPrinter(System.out);
    
    public TcpOutPipe(String name) {
        this.name = name;
        this.status = PipeStatus.newPipeStatus();
        this.status.setCapacity(20000);
        this.dataQueue = new ConcurrentLinkedQueue<E>();
        this.serializer = ClassUtil.newInstance(NetPipeCfg.instance.getPipeSerializerClassName());
    }
    
    @Override
    public boolean bind(String addr) {
    	Address address = Address.parse(addr);
    	if (address == null) {
    		return false;
    	}
    	
    	this.status.setAddress(address);
    	
        this.boundAddress = address.toSocketAddress();
        if (this.boundAddress == null) {
            return false;
        }
        
        try {
            this.boundSocket = new ServerSocket();
            boundSocket.bind(this.boundAddress);
            this.status.setConnected(true);
            logger.log("bind out pipe:%s", addr);
        } catch (IOException e) {
            logger.log("out pipe bind exception:", e);
            return false;
        }
        
        this.connections = new ArrayList<Connection>(); 
        
        this.listenThread = new Thread(new Listener());
        this.listenThread.start();
        
        this.sendThread = new Thread(new Sender());
        this.sendThread.start();
        
        return true;
    }
    
    public void stop() {
        if (this.boundSocket != null) {
            try {
                this.boundSocket.close();
            } catch (Exception e) {
                logger.log("outpipe close socket exception:", e);
            }
        }
    }
    
    public class Listener implements Runnable {
        @Override
        public void run() {
            try {
                Connection connection = new Connection(boundSocket.accept());
                connections.add(connection);
            } catch (Exception e) {
                logger.log("out pipe accept exception:", e);
            }
        }
    }
    
    public class Sender implements Runnable {
        @Override
        public void run() {
        	while (true) {
	            boolean slowDown = true;
	            for (Connection conn : connections) {
	                // too fast to send
	                if (conn.getCurrQps() > conn.getMaxQps()) {
	                    continue;
	                }
	                
	                E e = dataQueue.poll();
	                // queue is empty
	                if (e == null) {
	                    continue;
	                }
	                
	                status.setOutQps(status.getOutQps() + 1);
	                status.setSize(status.getSize() - 1);
	                
	                boolean success = trySend(conn.getSocket(), e);
	                if (success == false) {
	                    close(conn.getSocket());
	                    connections.remove(conn);
	                    continue;
	                }
	                
	                slowDown = false;
	                conn.incQps();
	            }
	            
	            // if all consumers reach max qps, io thread sleep 1 ms
	            if (slowDown) {
	                sleep();
	            }
        	}
        }
        
        private boolean trySend(Socket socket, E e) {
            try {
            	outBuffer.clear();
                serializer.encode(e, new ByteBufferOutputStream(outBuffer));
                outBuffer.flip();
                logger.log("out pipe send a element:%s", this.toString());
                socket.getOutputStream().write(outBuffer.array(), 0, outBuffer.limit());
                return true;
            } catch (Exception e1) {
                logger.log("send data from %s to %s exception", socket.getLocalSocketAddress().toString(), socket.getRemoteSocketAddress().toString());
                return false;
            }
        }
        
        private void close(Socket socket) {
            try {
                socket.close();
            } catch (Exception e) {
                logger.log("close socket %s exception", socket.getInetAddress().toString());
            }
        }
        
        private void sleep() {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                logger.log("out pipe io thread %s sleep is interrupted", name);
            }
        }
    }
    
    @Override
    public void write(E e) {
    	while (true) {
    		if (this.status.getSize() > this.status.getCapacity()) {
    			logger.log("write to out pipe queue failed: queue is full");
    			sleep(1000);
    			continue;
    		}
    		
    		dataQueue.add(e);
    		status.setInQps(status.getInQps() + 1);
    		status.setSize(status.getSize() + 1);
    		logger.log("write element to out pipe queue:%s", this.toString());
    		break;
    	}
    }
    
    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            logger.log("task write data %s sleep is interrupted", name);
        }
    }
    
    @Override
    public String name() {
        return name;
    }
    
    @Override
    public void resetQps() {
        for (Connection conn : connections) {
            conn.resetQps();
        }
        status.setInQps(0);
        status.setOutQps(0);
    }
    
    @Override
    public void setMaxQps(Address peer, int qps) {
        for (Connection conn : connections) {
            if (conn.getAddress().equals(peer)) {
                conn.maxQps = qps;
            }
        }
    }
    
    private class Connection {
        private Socket socket;
        private Address address;
        private int currQps; //  count of items sended every second
        private int maxQps; 
        
        public Connection(Socket s) {
            this.socket = s;
            this.currQps = 0;
            this.maxQps = 1000;
            this.address = Address.fromSocketAddress(socket.getRemoteSocketAddress());
        }
        
        public Address getAddress()  {
            return this.address;
        }
        
        public Socket getSocket() {
            return socket;
        }
        public int getCurrQps() {
            return this.currQps;
        }
        public int getMaxQps() {
            return this.maxQps;
        }
        public void incQps() {
            this.currQps++;
        }
        public void resetQps() {
            this.currQps = 0;
        }
    }

    
    @Override
    public PipeStatus getStatus() {
        return this.status;
    }
    
    @Override
    public String toString() {
    	return this.name;
    }
}
