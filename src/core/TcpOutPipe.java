package core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import msg.MsgType;
import util.Unit;
import cn.oasistech.util.Address;
import cn.oasistech.util.ByteUtil;
import cn.oasistech.util.Logger;

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
    
    
    private final byte[] outBuffer = new byte[1024 * 10];
    private int capacity = 128 * Unit.KB;
    private int inQps = 0;
    
    private final Logger logger = new Logger().addPrinter(System.out);
    
    public TcpOutPipe(String name) {
        this.name = name;
    }
    
    public boolean start(Address address) {
        this.boundAddress = address.toSocketAddress();
        if (this.boundAddress == null) {
            return false;
        }
        
        try {
            this.boundSocket = new ServerSocket();
            boundSocket.bind(this.boundAddress);
        } catch (IOException e) {
            logger.log("out pipe bind exception:", e);
            return false;
        }
        
        this.dataQueue = new ConcurrentLinkedQueue<E>();
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
        
        private boolean trySend(Socket socket, E e) {
            try {
                byte[] data = serializer.encode(e);
                int frameLength = data.length + TLVFrame.HeadLength;
                byte[] buffer = outBuffer;
                if (frameLength > outBuffer.length) {
                    buffer = new byte[frameLength];
                }
                
                ByteUtil.writeByBig(buffer, 0, (short)MsgType.Data.ordinal());
                ByteUtil.writeByBig(buffer, 2, (short)data.length);
                ByteUtil.copy(buffer, 4, data);
                
                socket.getOutputStream().write(buffer, 0, frameLength);
                return true;
            } catch (IOException e1) {
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
                Thread.sleep(1);
            } catch (Exception e) {
                logger.log("out pipe io thread %s sleep is interrupted", name);
            }
        }
    }
    
    @Override
    public void write(E e) {
        dataQueue.add(e);
        inQps++;
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
        inQps = 0;
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
            this.address = Address.fromTcpSocketAddress(socket.getRemoteSocketAddress());
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
}
