package core;

public interface OutPipe<E> {
    void write(E e);
    void writeInPipeCmd(PipeCmd cmd);
    PipeCmd readOutPipeCmd();
}