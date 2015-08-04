package core;

import cn.oasistech.util.Address;

public interface OutPipe<E> {
    String name();
    void write(E e);
    void resetQps();
    PipeStatus getStatus();
    void setMaxQps(Address peer, int qps);
}