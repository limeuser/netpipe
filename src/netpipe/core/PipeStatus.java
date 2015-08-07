package netpipe.core;

import mjoys.util.Address;

public class PipeStatus {
    private int inQps;
    private int outQps;
    private int size;
    private int capacity;
    private Address address;
    private boolean isConnected;
    
    public final static PipeStatus newPipeStatus() {
        PipeStatus status = new PipeStatus();
        status.setConnected(false);
        return status;
    }
    
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public int getInQps() {
        return inQps;
    }
    public void setInQps(int inQps) {
        this.inQps = inQps;
    }
    public int getOutQps() {
        return outQps;
    }
    public void setOutQps(int outQps) {
        this.outQps = outQps;
    }
    public boolean isConnected() {
        return isConnected;
    }
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
