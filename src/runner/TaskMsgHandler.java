package runner;

import java.util.List;

import msg.TaskCmd;
import msg.ValueType;

import cn.oasistech.util.Logger;
import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.IdFrame;
import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.agent.client.AgentRpcHandler;
import core.TLVFrame;

public class TaskMsgHandler implements AgentRpcHandler {
    private TaskRunner task;
    private final Logger logger = new Logger().addPrinter(System.out);
    
    @Override
    public void handle(AgentAsynRpc rpc, IdFrame idFrame) {
        if (idFrame.getId() != AgentProtocol.PublicService.Agent.id) {
            List<TLVFrame> frames = TLVFrame.parseTLVFrame(idFrame.getBody(), idFrame.getBodyLength());
            for (TLVFrame frame : frames) {
                if (frame.getType() == ValueType.CreateWorker.ordinal()) {
                    task.createWorker();
                } else if (frame.getType() == ValueType.DestroyWorkder.ordinal()) {
                    task.destoryWorker();
                } else if (frame.getType() == ValueType.SetMaxQps.ordinal()) {
                    TaskCmd.SetMaxQps setMaxQps = (TaskCmd.SetMaxQps) task.getAgentSerlizer().decode(frame.getValue());
                    task.setMaxQps(setMaxQps.getOutPipeName(), setMaxQps.getAddress(), setMaxQps.getQps());
                } else if (frame.getType() == ValueType.SwitchOutPipe.ordinal()) {
                    TaskCmd.SwitchOutPipe switchOutPipe = (TaskCmd.SwitchOutPipe) task.getAgentSerlizer().decode(frame.getValue());
                    task.switchOutPipe(switchOutPipe.getInPipeName(), switchOutPipe.getOutPipeAddress());
                } else if (frame.getType() == ValueType.ReportStatus.ordinal()) {
                	rpc.sendTo(idFrame.getId(), task.getAgentSerlizer().encode(task.getStatus()));
                }
            }
        } else {
            logger.log("agent response: %s", rpc.getParser().decodeResponse(idFrame.getBody()).toString());
        }
    }
}
