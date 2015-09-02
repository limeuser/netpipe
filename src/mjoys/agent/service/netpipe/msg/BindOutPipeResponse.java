package mjoys.agent.service.netpipe.msg;

import mjoys.util.Formater;

public class BindOutPipeResponse {
	private int taskId;
	private String outPipeName;
	private String outPipeAddress;
	private boolean result;
	
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public String getOutPipeName() {
		return outPipeName;
	}
	public void setOutPipeName(String outPipeName) {
		this.outPipeName = outPipeName;
	}
	public String getOutPipeAddress() {
		return outPipeAddress;
	}
	public void setOutPipeAddress(String outPipeAddress) {
		this.outPipeAddress = outPipeAddress;
	}
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return Formater.formatEntries("taskid", taskId, "outpipename", outPipeName, "address", outPipeAddress, "result", result);
	}
}
