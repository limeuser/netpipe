package msg;

import java.util.ArrayList;
import java.util.List;

public class TaskStatus {
    private String jobName;
    private String taskName;
    private int taskId;
    private int workerCount;
    private List<PipeStatus> pipeStatus = new ArrayList<PipeStatus>();
    
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<PipeStatus> getPipeStatus() {
        return pipeStatus;
    }

    public void setPipeStatus(List<PipeStatus> pipeStatus) {
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
}
