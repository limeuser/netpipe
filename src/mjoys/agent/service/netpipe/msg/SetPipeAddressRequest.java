package mjoys.agent.service.netpipe.msg;

import java.util.Map;

import mjoys.util.Formater;

public class SetPipeAddressRequest {
	private int taskId;
	private Map<String, String> inPipeAddresses;
	private Map<String, String> outPipeAddresses;
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public Map<String, String> getInPipeAddresses() {
		return inPipeAddresses;
	}
	public void setInPipeAddresses(Map<String, String> inPipeAddresses) {
		this.inPipeAddresses = inPipeAddresses;
	}
	public Map<String, String> getOutPipeAddresses() {
		return outPipeAddresses;
	}
	public void setOutPipeAddresses(Map<String, String> outPipeAddresses) {
		this.outPipeAddresses = outPipeAddresses;
	}
	
	@Override
	public String toString() {
		return Formater.formatEntries("taskid", taskId, "inpipes", Formater.formatMap(inPipeAddresses), "outpipes", Formater.formatMap(outPipeAddresses));
	}
}
