package manager;

import java.util.HashMap;
import java.util.Map;

import core.generator.TaskDes;

public class RunningTask {
    private TaskDes taskInfo;
    private Host host;
    
	private int id;
    private int pid;
    private int agentId;
    private int workerCount;
    private TaskRunningStatus runningStatus;
    
    private Map<String, InPipe> inPipes = new HashMap<String, InPipe>();
    private Map<String, OutPipe> outPipes = new HashMap<String, OutPipe>();

    public final static RunningTask newRunningTask(TaskDes taskInfo, int id, Host host) {
        RunningTask runningTask = new RunningTask();
        runningTask.setId(id);
        runningTask.setTaskInfo(taskInfo);
        runningTask.setHost(host);
        runningTask.setRunningStatus(TaskRunningStatus.Init);
        return runningTask;
    }
    
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getAgentId() {
		return agentId;
	}
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
    public TaskDes getTaskInfo() {
        return taskInfo;
    }
    public void setTaskInfo(TaskDes task) {
        this.taskInfo = task;
    }
    public TaskRunningStatus getRunningStatus() {
        return runningStatus;
    }
    public void setRunningStatus(TaskRunningStatus runningStatus) {
        this.runningStatus = runningStatus;
    }
    public int getWorkerCount() {
        return workerCount;
    }
    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }
    public Map<String, InPipe> getInPipes() {
        return inPipes;
    }
    public void setInPipes(Map<String, InPipe> inPipes) {
        this.inPipes = inPipes;
    }
    public Map<String, OutPipe> getOutPipes() {
        return outPipes;
    }
    public void setOutPipes(Map<String, OutPipe> outPipes) {
        this.outPipes = outPipes;
    }
    public Host getHost() {
        return host;
    }
    public void setHost(Host host) {
        this.host = host;
    }
}
