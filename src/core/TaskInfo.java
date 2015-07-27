package core;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.lang.reflect.Method;

public class TaskInfo {
    private Task task;
    private Method method;
    private Timer qpsTimer;
    private List<PipeInfo> inPipe = new ArrayList<PipeInfo>();
    private List<PipeInfo> outPipe = new ArrayList<PipeInfo>();
    
    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }
    public List<PipeInfo> getInPipe() {
        return inPipe;
    }
    public void setInPipe(List<PipeInfo> inPipe) {
        this.inPipe = inPipe;
    }
    public List<PipeInfo> getOutPipe() {
        return outPipe;
    }
    public void setOutPipe(List<PipeInfo> outPipe) {
        this.outPipe = outPipe;
    }
    public String name() {
        if (task.name().isEmpty()) {
            return method.getName();
        } else {
            return task.name();
        }
    }
}
