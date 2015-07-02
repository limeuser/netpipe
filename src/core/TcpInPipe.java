package core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import util.*;

public class TcpInPipe<E> implements InPipe<E> {
    private String outPipeAddress;
    private Socket socket;
    private byte[] buffer = new byte[2048];
    private BlockingQueue<E> dataQueue = new ArrayBlockingQueue<E>(128 * Unit.KB);
    private BlockingQueue<PipeCmd> cmdQueue = new ArrayBlockingQueue<PipeCmd>(1024);
    private Serializer serializer;
    private final Logger logger = new Logger().addPrinter(System.out);
    
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
            socket.getOutputStream().write(serializer.encode(cmd));
        } catch (Exception e) {
            logger.log("in pipe write cmd exception:", e);
        }
    }
    
    public TcpInPipe(String address) {
        try {
            this.outPipeAddress = address;
            socket.connect(null);
        } catch (Exception e) {
            logger.log("connect to out pipe exception: address=%s", e, outPipeAddress);
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
        int length = socket.getInputStream().read(buffer);
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
}
