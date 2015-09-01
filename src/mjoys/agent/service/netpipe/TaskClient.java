package mjoys.agent.service.netpipe;

import java.util.List;

import mjoys.agent.client.AgentAsynRpc;
import mjoys.agent.service.netpipe.msg.*;

public class TaskClient {
	private AgentAsynRpc rpc;
	public TaskClient(AgentAsynRpc rpc) {
		this.rpc = rpc;
	}
	
	public void getTaskStatus(int taskAgentId, int taskId) {
		GetTaskStatusRequest request = new GetTaskStatusRequest();
		request.setTaskId(taskId);
		rpc.sendMsg(taskAgentId, MsgType.GetTaskStatus.ordinal(), request);
	}
	
	public void switchOutPipe(int taskAgentId, int taskId, String inPipeName, String outPipeAddress) {
		SwitchOutPipeRequest request = new SwitchOutPipeRequest();
		request.setTaskId(taskId);
		request.setInPipeName(inPipeName);
		request.setOutPipeAddress(outPipeAddress);
		rpc.sendMsg(taskAgentId, MsgType.SwitchOutPipe.ordinal(), request);
	}
	
	public void setMaxQps(int taskAgentId, int taskId, String outPipeName, String inPipeAddress, int maxQps) {
		SetMaxQpsRequest request = new SetMaxQpsRequest();
		request.setTaskId(taskId);
		request.setOutPipeName(outPipeName);
		request.setInPipeAddress(inPipeAddress);
		request.setQps(maxQps);
		rpc.sendMsg(taskAgentId, MsgType.SetMaxQps.ordinal(), request);
	}
	
	public void bindOutPipe(int taskAgentId, int taskId, List<String> addresses) {
		BindOutPipeRequest request = new BindOutPipeRequest();
		request.setTaskId(taskId);
		request.setAddresses(addresses);
		rpc.sendMsg(taskAgentId, MsgType.BindOutPipe.ordinal(), request);
	}
	
	public void connectOutPipe(int taskAgentId, int taskId, String inPipeName, String outPipeAddress) {
		ConnectOutPipeRequest request = new ConnectOutPipeRequest();
		request.setTaskId(taskId);
		request.setInPipeName(inPipeName);
		request.setOutPipeAddress(outPipeAddress);
		rpc.sendMsg(taskAgentId, MsgType.ConnectOutPipe.ordinal(), request);
	}
	
	public void createWorker(int taskAgentId, int taskId) {
		rpc.sendMsg(taskAgentId, MsgType.CreateWorker.ordinal(), null);
	}
	
	public void destroyWorker(int taskAgentId, int taskId) {
		rpc.sendMsg(taskAgentId, MsgType.DestroyWorkder.ordinal(), null);
	}
}
