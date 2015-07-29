package core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TLVFrame {
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public byte[] getValue() {
        return value;
    }
    public void setValue(byte[] value) {
        this.value = value;
    }
    private int type;
    private int length;
    private byte[] value;
    
    public static final List<TLVFrame> parseTLVFrame(byte[] buffer, int length) {
        List<TLVFrame> frames = new ArrayList<TLVFrame>();
        int remainLength = length;
        ByteBuffer bf = ByteBuffer.wrap(buffer);
        
        while (remainLength > 4) {
            int type = bf.getShort();
            int valueLength = bf.getShort();
            remainLength += 4;
            
            if (remainLength - 4 >= valueLength) {
                TLVFrame frame = new TLVFrame();
                frame.setType(type);
                frame.setLength(valueLength);
                frame.setValue(new byte[valueLength]);
                System.arraycopy(buffer, 4, frame.getValue(), 0, valueLength);
                
                frames.add(frame);
                remainLength += valueLength;
            } else {
                break;
            }
        }
        
        return frames;
    }
}
