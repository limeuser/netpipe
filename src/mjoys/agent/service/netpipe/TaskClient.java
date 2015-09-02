package mjoys.agent.service.netpipe;

import java.util.Map;

import mjoys.agent.client.AgentAsynRpc;
import mjoys.agent.service.netpipe.msg.ConnectOutPipeRequest;
import mjoys.agent.service.netpipe.msg.GetTaskStatusRequest;
import mjoys.agent.service.netpipe.msg.MsgType;
import mjoys.agent.service.netpipe.msg.SetMaxQpsRequest;
import mjoys.agent.service.netpipe.msg.SetPipeAddressRequest;
import mjoys.agent.service.netpipe.msg.SwitchOutPipeRequest;
import mjoys.util.Logger;

public class TaskClient {
	private AgentAsynRpc rpc;
	private final static Logger logger = new Logger().addPrinter(System.out);
	
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
	
	public void setPipeAddress(int taskAgentId, int taskId, Map<String, String> ins, Map<String, String> outs) {
		SetPipeAddressRequest request = new SetPipeAddressRequest();
		request.setTaskId(taskId);
		request.setInPipeAddresses(ins);
		request.setOutPipeAddresses(outs);
		rpc.sendMsg(taskAgentId, MsgType.SetPipeAddress.ordinal(), request);
		logger.log("set pipe address[agentid=%d, taskid=%d]:%s", taskAgentId, taskId, request.toString());
	}
	
	public void setPipeAddress(int taskAgentId, int taskId, String inPipeName, String outPipeAddress) {
		ConnectOutPipeRequest request = new ConnectOutPipeRequest();
		request.setTaskId(taskId);
		request.setInPipeName(inPipeName);
		request.setOutPipeAddress(outPipeAddress);
		rpc.sendMsg(taskAgentId, MsgType.SetPipeAddress.ordinal(), request);
	}
	
	public void createWorker(int taskAgentId, int taskId) {
		rpc.sendMsg(taskAgentId, MsgType.CreateWorker.ordinal(), null);
	}
	
	public void destroyWorker(int taskAgentId, int taskId) {
		rpc.sendMsg(taskAgentId, MsgType.DestroyWorkder.ordinal(), null);
	}
}
