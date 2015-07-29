package core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import cn.oasistech.util.Address;
import util.Logger;
import util.Unit;

public class TcpOutPipe<E> implements OutPipe<E> {
    private String name;
    
    public TcpOutPipe(String name) {
        this.name = name;
    }
    
    private Serializer serializer;
    
    private ServerSocket boundSocket;
    private InetSocketAddress boundAddress;
    private List<Connection> connections;
    
    private Thread listenThread;
    private Thread sendThread;
    
    private BlockingQueue<E> dataQueue;
    
    private int capacity = 128 * Unit.KB;
    
    private final Logger logger = new Logger().addPrinter(System.out);
    
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
        
        this.dataQueue = new ArrayBlockingQueue<E>(capacity);
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
                
                E e = take();
                if (e != null) {
                    boolean success = trySend(conn.getSocket(), e);
                    if (!success) {
                        close(conn.getSocket());
                        connections.remove(conn);
                    }
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
                socket.getOutputStream().write(serializer.encode(e));
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
        
        private E take() {
            try {
                return dataQueue.take();
            } catch (Exception e) {
                logger.log("take data from out pipe queue %s exception", name);
                return null;
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
    }
    
    @Override
    public String name() {
        return name;
    }
    
    @Override
    public void resetStat() {
        for (Connection conn : connections) {
            conn.resetQps();
        }
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
    public int size() {
        return dataQueue.size();
    }
    
    @Override
    public int capacity() {
        return capacity;
    }
}
