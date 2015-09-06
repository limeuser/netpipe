package mjoys.agent.service.netpipe.msg;

import mjoys.util.Formater;

public class GetTaskStatusRequest {
	private int taskId;

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public String toString() {
		return Formater.formatEntry("taskId", taskId);
	}
}
