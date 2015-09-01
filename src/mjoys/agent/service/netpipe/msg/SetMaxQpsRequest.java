package mjoys.agent.service.netpipe.msg;

public class SetMaxQpsRequest {
    private String outPipeName;
    private String inPipeAddress;
    private int qps;
	private int taskId;

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
    
    public int getQps() {
        return qps;
    }
    public void setQps(int qps) {
        this.qps = qps;
    }
    public String getOutPipeName() {
        return outPipeName;
    }
    public void setOutPipeName(String outPipeName) {
        this.outPipeName = outPipeName;
    }

	public String getInPipeAddress() {
		return inPipeAddress;
	}

	public void setInPipeAddress(String inPipeAddress) {
		this.inPipeAddress = inPipeAddress;
	}
}
