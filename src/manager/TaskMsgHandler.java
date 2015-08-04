package manager;

import java.util.List;
import java.util.Map;

import msg.TaskStatus;
import msg.MsgType;
import core.AgentTag;
import core.Serializer;
import core.Service;
import core.TLVFrame;
import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.NotifyConnectionResponse;
import cn.oasistech.agent.Response;
import cn.oasistech.agent.IdFrame;
import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.agent.client.AgentRpcHandler;
import cn.oasistech.util.Logger;
import cn.oasistech.util.NumberUtil;
import cn.oasistech.util.Tag;

public class TaskMsgHandler implements AgentRpcHandler {
    private Host host;
    private Serializer serializer;
    private final static Logger logger = new Logger().addPrinter(System.out);
    
    public TaskMsgHandler(Host host) {
        this.host = host;
    }
    
	@Override
	public void handle(AgentAsynRpc rpc, IdFrame idFrame) {
		if (idFrame.getId() == AgentProtocol.PublicService.Agent.id) {
		    processAgentMsg(rpc, idFrame);
		} else {
		    processTaskMsg(rpc, idFrame);
		}
	}
	
	private void processAgentMsg(AgentAsynRpc rpc, IdFrame idFrame) {
	    Response response = rpc.getParser().decodeResponse(idFrame.getBody());
        if (response == null) {
            logger.log("response is null");
            return;
        } else if (!response.getError().equals(AgentProtocol.Error.Success.name())) {
            logger.log("return error: msg=%s, error=%s", response.getType(), response.getError());
            return;
        }
        
        // running task connect to agent
        if (response.getType().equals(AgentProtocol.MsgType.NotifyConnection.name())) {
            NotifyConnectionResponse connectionResponse = (NotifyConnectionResponse)response;
            Map<String, String> tags = Tag.toMap(connectionResponse.getIdTag().getTags());
            String service = tags.get(AgentProtocol.PublicTag.servicename.name());
            if (service != null && service.equals(Service.dpipe_task.name())) {
                int taskId = NumberUtil.parseInt(tags.get(AgentTag.dpipe_id.name()));
                int agentId = idFrame.getId();
                host.runningTaskConnected(taskId, agentId);
            }
        }
	}
	
	private void processTaskMsg(AgentAsynRpc rpc, IdFrame idFrame) {
	    List<TLVFrame> frames = TLVFrame.parseTLVFrame(idFrame.getBody(), idFrame.getBodyLength());
        for (TLVFrame frame : frames) {
            if (frame.getType() == MsgType.ReportStatus.ordinal()) {
                TaskStatus status = (TaskStatus) serializer.decode(frame.getValue());
                host.updateRunningTaskStatus(status);
            }
        }
	}
}
