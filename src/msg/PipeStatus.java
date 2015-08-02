package msg;

public class PipeStatus {
    private String name;
    private int size;
    private int capacity;
    private int inQps;
    private int outQps;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
}
