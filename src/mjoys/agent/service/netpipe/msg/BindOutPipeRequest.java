package mjoys.agent.service.netpipe.msg;

import java.util.List;

public class BindOutPipeRequest {
	private int taskId;
	private List<String> addresses;
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public List<String> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}
}
