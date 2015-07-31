package core;

public interface InPipe<E> {
    String name();
    E read();
    void switchOutPipe(String outPipeAddress);
    int inQps();
    int outQps();
    void resetStat();
    int size();
    int capacity();
}