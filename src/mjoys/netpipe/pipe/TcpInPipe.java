package mjoys.netpipe.pipe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.AbstractQueue;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import mjoys.frame.ByteBufferParser;
import mjoys.frame.LV;
import mjoys.io.ByteBufferInputStream;
import mjoys.io.Serializer;
import mjoys.io.SerializerException;
import mjoys.socket.tcp.client.SocketClient;
import mjoys.util.Address;
import mjoys.util.Logger;

public class TcpInPipe<E> implements InPipe<E> {
    private String name;
    private PipeStatus status;
    
    private Thread readThread;
    private SocketClient client;
    private Serializer serializer;
    private AbstractQueue<E> dataQueue;
    
    private byte[] buffer = new byte[2048];
    private final Logger logger = new Logger().addPrinter(System.out);
    
    public TcpInPipe(String name) {
        this.name = name;
        this.status = PipeStatus.newPipeStatus();
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
        
        this.status.setConnected(true);
        return true;
    }
    
    public void stop() {
        if (this.client != null) {
            this.client.disconnect();
        }
        this.status.setConnected(false);
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
    
    @SuppressWarnings("unchecked")
    private void doReadFrame() throws IOException, SerializerException {
        int length = client.recv(buffer);
        if (length <= 0) {
            return;
        }
        
        List<LV<ByteBuffer>> lvs = ByteBufferParser.parseLVs(ByteBuffer.wrap(buffer, 0, length));
        for (LV<ByteBuffer> lv : lvs) {
            dataQueue.offer((E) serializer.decode(new ByteBufferInputStream(lv.body), Object.class));
            this.status.setInQps(this.status.getInQps() + 1);
        }
    }
    
    @Override
    public E read() {
        while (true) {
            E e = dataQueue.poll();
            if (e != null) {
                this.status.setOutQps(this.status.getOutQps() + 1);
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
    public void switchOutPipe(String outPipeAddress) {
        client.reconnect(Address.parse(outPipeAddress));
    }

    @Override
    public void resetQps() {
        this.status.setInQps(0);
        this.status.setOutQps(0);
    }
    
    @Override
    public PipeStatus getStatus() {
        return this.status;
    }
}
