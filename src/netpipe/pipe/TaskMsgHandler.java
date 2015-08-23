package netpipe.pipe;

import java.nio.ByteBuffer;

import mjoys.frame.TLV;
import mjoys.frame.TV;
import mjoys.io.ByteBufferInputStream;
import mjoys.util.Logger;
import netpipe.msg.MsgType;
import netpipe.msg.TaskMsg;
import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.agent.client.AgentRpcHandler;

public class TaskMsgHandler implements AgentRpcHandler<ByteBuffer> {
    private TaskRunner task;
    private final Logger logger = new Logger().addPrinter(System.out);
    
    @Override
    public void handle(AgentAsynRpc rpc, TLV<ByteBuffer> idFrame) {
    	TV<ByteBuffer> msgFrame = AgentProtocol.parseMsgFrame(idFrame.body);
        if (idFrame.tag != AgentProtocol.PublicService.Agent.id) {
           try {
        	   handleMsg(rpc, idFrame.tag, msgFrame);
           } catch (Exception e) {
        	   logger.log("serializer exception", e);
           }
        } else {
            logger.log("agent response: %s", AgentProtocol.decodeAgentResponse(AgentProtocol.getMsgType(msgFrame.tag), new ByteBufferInputStream(msgFrame.body), rpc.getSerializer()));
        }
    }
    
    private void handleMsg(AgentAsynRpc rpc, int id, TV<ByteBuffer> msgFrame) throws Exception {
    	 if (msgFrame == null) {
         	logger.log("cant' get msg frame");
         	return;
         }
         
         if (msgFrame.tag == MsgType.CreateWorker.ordinal()) {
             task.createWorker();
         } else if (msgFrame.tag == MsgType.DestroyWorkder.ordinal()) {
             task.destoryWorker();
         } else if (msgFrame.tag == MsgType.SetMaxQps.ordinal()) {
             TaskMsg.SetMaxQps setMaxQps = (TaskMsg.SetMaxQps) task.getAgentSerlizer().decode(new ByteBufferInputStream(msgFrame.body), TaskMsg.SetMaxQps.class);
             task.setMaxQps(setMaxQps.getOutPipeName(), setMaxQps.getAddress(), setMaxQps.getQps());
         } else if (msgFrame.tag == MsgType.SwitchOutPipe.ordinal()) {
             TaskMsg.SwitchOutPipe switchOutPipe = (TaskMsg.SwitchOutPipe) task.getAgentSerlizer().decode(new ByteBufferInputStream(msgFrame.body), TaskMsg.SwitchOutPipe.class);
             task.switchOutPipe(switchOutPipe.getInPipeName(), switchOutPipe.getOutPipeAddress());
         } else if (msgFrame.tag == MsgType.ReportStatus.ordinal()) {
         	rpc.sendMsg(id, msgFrame.tag, task.getStatus());
         }
    }
}
