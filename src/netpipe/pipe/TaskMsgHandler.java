package netpipe.pipe;

import java.nio.ByteBuffer;

import mjoys.util.Logger;
import mjoys.util.TLVFrame;
import netpipe.msg.MsgType;
import netpipe.msg.TaskMsg;
import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.IdFrame;
import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.agent.client.AgentRpcHandler;

public class TaskMsgHandler implements AgentRpcHandler {
    private TaskRunner task;
    private final Logger logger = new Logger().addPrinter(System.out);
    
    @Override
    public void handle(AgentAsynRpc rpc, IdFrame idFrame) {
        if (idFrame.getId() != AgentProtocol.PublicService.Agent.id) {
            TLVFrame frame = TLVFrame.parseTLV(ByteBuffer.wrap(idFrame.getBody(), 0, idFrame.getBodyLength()));
            
            if (frame.getType() == MsgType.CreateWorker.ordinal()) {
                task.createWorker();
            } else if (frame.getType() == MsgType.DestroyWorkder.ordinal()) {
                task.destoryWorker();
            } else if (frame.getType() == MsgType.SetMaxQps.ordinal()) {
                TaskMsg.SetMaxQps setMaxQps = (TaskMsg.SetMaxQps) task.getAgentSerlizer().decode(frame.getValue());
                task.setMaxQps(setMaxQps.getOutPipeName(), setMaxQps.getAddress(), setMaxQps.getQps());
            } else if (frame.getType() == MsgType.SwitchOutPipe.ordinal()) {
                TaskMsg.SwitchOutPipe switchOutPipe = (TaskMsg.SwitchOutPipe) task.getAgentSerlizer().decode(frame.getValue());
                task.switchOutPipe(switchOutPipe.getInPipeName(), switchOutPipe.getOutPipeAddress());
            } else if (frame.getType() == MsgType.ReportStatus.ordinal()) {
            	rpc.sendTo(idFrame.getId(), task.getAgentSerlizer().encode(task.getStatus()));
            }
        } else {
            logger.log("agent response: %s", rpc.getSerializer().decodeResponse(idFrame.getBody()).toString());
        }
    }
}
