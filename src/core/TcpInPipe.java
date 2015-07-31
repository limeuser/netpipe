package core;

import java.io.IOException;
import java.util.AbstractQueue;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.oasistech.util.SocketClient;
import cn.oasistech.util.Logger;
import util.Unit;
import cn.oasistech.util.Address;

public class TcpInPipe<E> implements InPipe<E> {
    private String name;
    private Serializer serializer;
    private SocketClient client;
    private Thread readThread;
    private AbstractQueue<E> dataQueue;
    
    private int capacity = 128 * Unit.KB;
    
    private int inQps = 0;
    private int outQps = 0;
    
    private byte[] buffer = new byte[2048];
    
    private final Logger logger = new Logger().addPrinter(System.out);
    
    public TcpInPipe(String name) {
        this.name = name;
    }
    
    public boolean start(Address address) {
        this.dataQueue = new ConcurrentLinkedQueue<E>();
        
        this.client = new SocketClient();
        if (this.client.connect(address) == false) {
            this.client = null;
            return false;
        }
        
        this.readThread = new Thread(new Reader());
        readThread.start();
        
        return true;
    }
    
    public void stop() {
        if (this.client != null) {
            this.client.disconnect();
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
                dataQueue.offer((E) serializer.decode(frame.getValue()));
                inQps++;
            } else {
                logger.log("invalid message type:%d", frame.getType());
            }
        }
    }
    
    @Override
    public E read() {
        while (true) {
            E e = dataQueue.poll();
            if (e != null) {
                outQps++;
                return e;
            }
            
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
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
    
    @Override
    public int inQps() {
        return inQps;
    }
    
    @Override
    public int outQps() {
        return outQps;
    }
    
    @Override
    public void resetStat() {
        inQps = 0;
        outQps = 0;
    }
    
    @Override
    public void switchOutPipe(String outPipeAddress) {
        client.reconnect(Address.parse(outPipeAddress));
    }
}
