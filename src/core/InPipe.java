package core;

public interface InPipe<E> {
    String name();
    E read();
    int size();
    int capacity();
}