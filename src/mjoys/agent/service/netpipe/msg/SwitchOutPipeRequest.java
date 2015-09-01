package mjoys.agent.service.netpipe.msg;

public class SwitchOutPipeRequest {
	private int taskId;
    private String inPipeName;
    private String outPipeAddress;
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
    public String getInPipeName() {
        return inPipeName;
    }
    public void setInPipeName(String inPipeName) {
        this.inPipeName = inPipeName;
    }
    public String getOutPipeAddress() {
        return outPipeAddress;
    }
    public void setOutPipeAddress(String outPipeAddress) {
        this.outPipeAddress = outPipeAddress;
    }
}
