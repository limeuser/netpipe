package mjoys.agent.service.netpipe;

import mjoys.agent.client.AgentAsynRpc;
import mjoys.agent.service.netpipe.msg.MsgType;
import mjoys.agent.service.netpipe.msg.BindOutPipeResponse;

public class NetPipeClient {
	private AgentAsynRpc rpc;
	public NetPipeClient(AgentAsynRpc rpc) {
		this.rpc = rpc;
	}
}
