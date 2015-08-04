package manager;

import core.PipeStatus;


public class InPipe {
    private RunningTask task;
    
    private OutPipe out;
    private String name;
    private PipeStatus status;
    
    public final static InPipe newInPipe(RunningTask task, String name) {
        InPipe in = new InPipe();
        in.task = task;
        in.name = name;
        in.status = new PipeStatus();
        in.status.setStatus(PipeRunningStatus.disconnected);
        return in;
    }
    
    public RunningTask getTask() {
        return task;
    }

    public void setTask(RunningTask task) {
        this.task = task;
    }

    public OutPipe getOut() {
        return out;
    }

    public void setOut(OutPipe out) {
        this.out = out;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public PipeStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(PipeStatus status) {
        this.status = status;
    }
}
