package core;

public interface InPipe<E> {
    E read();
    PipeCmd readInPipeCmd();
    void writeOutPipeCmd(PipeCmd cmd);
}