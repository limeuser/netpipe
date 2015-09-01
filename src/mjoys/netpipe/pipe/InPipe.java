package mjoys.netpipe.pipe;

public interface InPipe<E> {
	boolean connect(String outPipeAddress);
    E read();
    String name();
    void resetQps();
    PipeStatus getStatus();
    void switchOutPipe(String outPipeAddress);
}