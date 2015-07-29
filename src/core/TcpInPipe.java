package core;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import cn.oasistech.util.Address;
import util.Logger;
import util.SocketClient;
import util.Unit;

public class TcpInPipe<E> implements InPipe<E> {
    private String name;
    private Serializer serializer;
    private SocketClient client;
    private Thread readThread;
    private BlockingQueue<E> dataQueue;
    
    private int capacity = 128 * Unit.KB;
    
    private byte[] buffer = new byte[2048];
    
    private final Logger logger = new Logger().addPrinter(System.out);
    
    public TcpInPipe(String name) {
        this.name = name;
    }
    
    public boolean start(Address address) {
        this.dataQueue = new ArrayBlockingQueue<E>(capacity);
        
        this.client = new SocketClient();
        if (this.client.start(address) == false) {
            this.client = null;
            return false;
        }
        
        this.readThread = new Thread(new Reader());
        readThread.start();
        
        return true;
    }
    
    public void stop() {
        if (this.client != null) {
            this.client.stop();
        }
    }
    
    // io thread, read frame and push frame to queue
    public class Reader implements Runnable {
        @Override
        public void run() {
            while (true) {
                readFrame();
            }
        }
    }
    
    private void readFrame() {
        try {
            doReadFrame();
        } catch (Exception e) {
            logger.log("in pipe read data exception:", e);
        }
    }
    
    private void doReadFrame() throws IOException, InterruptedException {
        int length = client.recv(buffer);
        List<TLVFrame> frames = TLVFrame.parseTLVFrame(buffer, length);
        for (TLVFrame frame : frames) {
            if (frame.getType() == ValueType.Data.ordinal()) {
                dataQueue.put((E) serializer.decode(frame.getValue()));
            } else {
                logger.log("invalid message type:%d", frame.getType());
            }
        }
    }
    
    @Override
    public E read() {
        while (true) {
            try {
                return dataQueue.take();
            } catch (InterruptedException e) {
                logger.log("data queue take interrupted");
            } 
        }
    }

    @Override
    public String name() {
        return name;
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
