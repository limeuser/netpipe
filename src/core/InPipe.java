package core;

public interface InPipe<E> {
    String name();
    E read();
    PipeCmd readInPipeCmd();
    void writeOutPipeCmd(PipeCmd cmd);
}