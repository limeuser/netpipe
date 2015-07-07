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
    private InetSocketAddress address;
    private Serializer serializer;
    private ServerSocket socket;
    private List<Socket> connections;
    private byte[] buffer = new byte[2048];
    private Thread listenThread = new Thread(new Listener());
    private Thread sendThread = new Thread(new Sender());
    private BlockingQueue<E> dataQueue = new ArrayBlockingQueue<E>(128 * Unit.KB);
    private BlockingQueue<PipeCmd> cmdQueue = new ArrayBlockingQueue<PipeCmd>(1024);
    
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
        
        this.connections = new ArrayList<Socket>(); 
        
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
                Socket connection = socket.accept();
                connections.add(connection);
            } catch (Exception e) {
                logger.log("out pipe accept exception:", e);
            }
        }
    }
    
    public class Sender implements Runnable {
        @Override
        public void run() {
            try {
                for (Socket conn : connections) {
                    E e = dataQueue.take();
                    conn.getOutputStream().write(serializer.encode(e));
                }
            }
            catch (Exception e) {
                logger.log("send data exception:", e);
            }
        }
    }
    
    public class CmdHandler implements Runnable {
        private Socket socket;
        private byte[] buffer = new byte[2048];
        
        @Override
        public void run() {
            try {
                int length = this.socket.getInputStream().read(buffer);
                byte[] data = new byte[length];
                System.arraycopy(data, length, buffer, 0, length);
                PipeCmd cmd = (PipeCmd)serializer.decode(data);
                if (cmd.getType() == PipeCmdType.SlowDown) {
                    
                } else if (cmd.getType() == PipeCmdType.SpeedUp) {
                    
                }
            } catch (Exception e) {
                logger.log("read outpipe connection data exception:", e);
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

}
