package core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import util.Address;
import util.Logger;
import util.SocketClient;
import util.Unit;
import core.TcpInPipe.Reader;

public class TcpOutPipe<E> implements OutPipe<E> {
    private String name;
    
    public TcpOutPipe(String name) {
        this.name = name;
    }
    
    private class Connection {
        private Socket socket;
        private int currQps; //  count of items sended every second
        private int maxQps; 
        
        public Connection(Socket s) {
            this.socket = s;
            this.currQps = 0;
            this.maxQps = 0;
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
    
    private InetSocketAddress address;
    private Serializer serializer;
    private ServerSocket socket;
    private Connection minDelayConnection;
    private List<Connection> connections;
    private Thread listenThread = new Thread(new Listener());
    private Thread sendThread = new Thread(new Sender());
    private BlockingQueue<E> dataQueue = new ArrayBlockingQueue<E>(128 * Unit.KB);
    
    private final Logger logger = new Logger().addPrinter(System.out);
    
    public boolean start(Address address) {
        this.address = address.toSocketAddress();
        if (this.address == null) {
            return false;
        }
        
        try {
            this.socket = new ServerSocket();
            socket.bind(this.address);
        } catch (IOException e) {
            logger.log("out pipe bind exception:", e);
            return false;
        }
        
        this.connections = new ArrayList<Connection>(); 
        
        this.listenThread.start();
        this.sendThread.start();
        
        return true;
    }
    
    public void stop() {
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (Exception e) {
                logger.log("outpipe close socket exception:", e);
            }
        }
    }
    
    public class Listener implements Runnable {
        @Override
        public void run() {
            try {
                Connection connection = new Connection(socket.accept());
                connections.add(connection);
                if (minDelayConnection == null) {
                    minDelayConnection = connection;
                }
            } catch (Exception e) {
                logger.log("out pipe accept exception:", e);
            }
        }
    }
    
    public class Sender implements Runnable {
        @Override
        public void run() {
            try {
                boolean slowDown = true;
                for (Connection conn : connections) {
                    // too fast to send
                    if (conn.getCurrQps() > conn.getMaxQps()) {
                        continue;
                    }
                    
                    E e = dataQueue.take();
                    conn.getSocket().getOutputStream().write(serializer.encode(e));
                    
                    slowDown = false;
                    conn.incQps();
                }
                
                // if all consumers reach max qps, io thread must sleep
                if (slowDown) {
                    Thread.sleep(1);
                }
            }
            catch (Exception e) {
                logger.log("send data exception:", e);
            }
        }
    }
    
    @Override
    public void write(E e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeInPipeCmd(PipeCmd cmd) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public PipeCmd readOutPipeCmd() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String name() {
        return name;
    }
}
