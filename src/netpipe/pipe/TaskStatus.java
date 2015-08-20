package netpipe.pipe;

import java.util.Map;
import java.util.HashMap;

public class TaskStatus {
    private int taskId;
    private int workerCount;
    private boolean isConnected;
    private Map<String, PipeStatus> pipeStatus = new HashMap<String, PipeStatus>();
    
    public Map<String, PipeStatus> getPipeStatus() {
        return pipeStatus;
    }

    public void setPipeStatus(Map<String, PipeStatus> pipeStatus) {
        this.pipeStatus = pipeStatus;
    }
    
    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
