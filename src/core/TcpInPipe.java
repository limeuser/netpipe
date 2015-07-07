package core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import util.Address;
import util.Logger;
import util.SocketClient;
import util.Unit;

public class TcpInPipe<E> implements InPipe<E> {
    private Serializer serializer;
    private byte[] buffer = new byte[2048];
    private long readedFrameCount = 0;
    private SocketClient client = new SocketClient();
    private Thread readThread = new Thread(new Reader());
    private BlockingQueue<E> dataQueue = new ArrayBlockingQueue<E>(128 * Unit.KB);
    private BlockingQueue<PipeCmd> cmdQueue = new ArrayBlockingQueue<PipeCmd>(1024);
    
    private final Logger logger = new Logger().addPrinter(System.out);
    
    public boolean start(Address address) {
        if (this.client.start(address) == false) {
            this.client = null;
            return false;
        }
        
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
                readedFrameCount++;
                // monitor queue size to implement backpress process
                if (dataQueue.remainingCapacity() < 20480) {
                    PipeCmd cmd = new PipeCmd();
                    cmd.setType(PipeCmdType.SlowDown);
                    writeOutPipeCmd(cmd);
                }
                
                if (dataQueue.size() * 3 < dataQueue.remainingCapacity()) {
                    if (readedFrameCount % 10000 == 0) {
                        PipeCmd cmd = new PipeCmd();
                        cmd.setType(PipeCmdType.SpeedUp);
                        writeOutPipeCmd(cmd);
                    }
                }
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
        if (length > 4) {
            ByteBuffer bf = ByteBuffer.wrap(buffer);
            int type = bf.getShort();
            int valueLength = bf.getShort();
            
            if (length - 4 >= valueLength) {
                TLVFrame frame = new TLVFrame();
                frame.setType(type);
                frame.setLength(valueLength);
                frame.setValue(new byte[valueLength]);
                System.arraycopy(buffer, 4, frame.getValue(), 0, valueLength);
                
                Object value = serializer.decode(frame.getValue());
                if (type == ValueType.Data.ordinal()) {
                    dataQueue.put((E) value);
                } else if (type == ValueType.Cmd.ordinal()) {
                    cmdQueue.put((PipeCmd) value);
                } else {
                    logger.log("invalid message type:%d", type);
                }
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
    public PipeCmd readInPipeCmd() {
        while (true) {
            try {
                return cmdQueue.take();
            } catch (InterruptedException e) {
                logger.log("cmd queue take interrupted");
            } 
        }
    }

    @Override
    public void writeOutPipeCmd(PipeCmd cmd) {
        try {
            client.send(serializer.encode(cmd));
        } catch (Exception e) {
            logger.log("in pipe write cmd exception:", e);
        }
    }
}
