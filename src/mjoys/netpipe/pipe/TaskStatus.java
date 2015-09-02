package mjoys.netpipe.pipe;

import java.util.Map;
import java.util.HashMap;

import mjoys.util.Formater;

public class TaskStatus {
    private int taskId;
    private int workerCount;
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
    
    @Override
    public String toString() {
    	return Formater.formatEntries("taskid", taskId, "workerCount", workerCount, "pipeStatus", Formater.formatMap(pipeStatus));
    }
}
