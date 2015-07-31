package core;

import cn.oasistech.util.Address;


public interface OutPipe<E> {
    String name();
    void write(E e);
    void resetStat();
    int inQps();
    int outQps();
    int size();
    int capacity();
    void setMaxQps(Address peer, int qps);
}