package mjoys.netpipe.pipe;

import java.nio.ByteBuffer;

import mjoys.agent.Agent;
import mjoys.agent.client.AgentAsynRpc;
import mjoys.agent.client.AgentRpcHandler;
import mjoys.frame.TLV;
import mjoys.frame.TV;
import mjoys.io.ByteBufferInputStream;
import mjoys.netpipe.msg.MsgType;
import mjoys.netpipe.msg.TaskMsg;
import mjoys.util.Logger;

public class TaskMsgHandler implements AgentRpcHandler<ByteBuffer> {
    private TaskRunner task;
    private final Logger logger = new Logger().addPrinter(System.out);
    
    @Override
    public void handle(AgentAsynRpc rpc, TLV<ByteBuffer> idFrame) {
    	TV<ByteBuffer> msgFrame = Agent.parseMsgFrame(idFrame.body);
        if (idFrame.tag != Agent.PublicService.Agent.id) {
           try {
        	   handleMsg(rpc, idFrame.tag, msgFrame);
           } catch (Exception e) {
        	   logger.log("serializer exception", e);
           }
        } else {
            logger.log("agent response: %s", Agent.decodeAgentResponse(Agent.getMsgType(msgFrame.tag), new ByteBufferInputStream(msgFrame.body), rpc.getSerializer()));
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
