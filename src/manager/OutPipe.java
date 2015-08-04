package manager;

import java.util.ArrayList;
import java.util.List;

import core.PipeStatus;
import cn.oasistech.util.Address;

public class OutPipe {
    private RunningTask task;
    
    private String name;
    private Address address;
    private PipeStatus status;
    private List<InPipe> ins = new ArrayList<InPipe>();
    
    public final static OutPipe newOutPipe(RunningTask task, String name, Address address) {
        OutPipe out = new OutPipe();
        out.task = task;
        out.name = name;
        out.address = address;
        out.status = new PipeStatus();
        out.status.setStatus(PipeRunningStatus.disconnected);
        return out;
    }

    public RunningTask getTask() {
        return task;
    }

    public void setTask(RunningTask task) {
        this.task = task;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public List<InPipe> getIns() {
        return ins;
    }

    public void setIns(List<InPipe> ins) {
        this.ins = ins;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }
    
    public Address getAddress() {
        return address;
    }
}
