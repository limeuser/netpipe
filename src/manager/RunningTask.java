package manager;

import java.util.ArrayList;
import java.util.List;

public class RunningTask {
	private int id;
    private int pid;
    private int agentId;
    private int workerCount;
    private TaskStatus status;
	private List<InPipe> ins = new ArrayList<InPipe>();
    private List<OutPipe> outs = new ArrayList<OutPipe>();

    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getWorkerCount() {
		return workerCount;
	}
	public void setWorkerCount(int workerCount) {
		this.workerCount = workerCount;
	}
	public List<InPipe> getIns() {
		return ins;
	}
	public void setIns(List<InPipe> ins) {
		this.ins = ins;
	}
	public List<OutPipe> getOuts() {
		return outs;
	}
	public void setOuts(List<OutPipe> outs) {
		this.outs = outs;
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
	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
}
