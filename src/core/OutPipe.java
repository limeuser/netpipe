package core;

public interface OutPipe<E> {
    String name();
    void write(E e);
    void writeInPipeCmd(PipeCmd cmd);
    PipeCmd readOutPipeCmd();
}