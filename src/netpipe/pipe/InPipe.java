package netpipe.pipe;

public interface InPipe<E> {
    E read();
    String name();
    void resetQps();
    PipeStatus getStatus();
    void switchOutPipe(String outPipeAddress);
}