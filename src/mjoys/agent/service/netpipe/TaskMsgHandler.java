package mjoys.agent.service.netpipe;

import java.nio.ByteBuffer;

import mjoys.agent.Agent;
import mjoys.agent.client.AgentAsynRpc;
import mjoys.agent.client.AgentRpcHandler;
import mjoys.agent.service.netpipe.msg.*;
import mjoys.frame.TLV;
import mjoys.frame.TV;
import mjoys.io.ByteBufferInputStream;
import mjoys.util.Logger;

public class TaskMsgHandler implements AgentRpcHandler<ByteBuffer> {
    private TaskServer task;
    private final Logger logger = new Logger().addPrinter(System.out);
    
    public TaskMsgHandler(TaskServer task) {
    	this.task = task;
    }
    
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
             SetMaxQpsRequest setMaxQps = rpc.getSerializer().decode(new ByteBufferInputStream(msgFrame.body), SetMaxQpsRequest.class);
             task.setMaxQps(setMaxQps.getOutPipeName(), setMaxQps.getInPipeAddress(), setMaxQps.getQps());
         } else if (msgFrame.tag == MsgType.SwitchOutPipe.ordinal()) {
             SwitchOutPipeRequest switchOutPipe = rpc.getSerializer().decode(new ByteBufferInputStream(msgFrame.body), SwitchOutPipeRequest.class);
             task.switchOutPipe(switchOutPipe.getInPipeName(), switchOutPipe.getOutPipeAddress());
         } else if (msgFrame.tag == MsgType.GetTaskStatus.ordinal()) {
         	 rpc.sendMsg(id, msgFrame.tag, task.getStatus());
         } else if (msgFrame.tag == MsgType.SetPipeAddress.ordinal()) {
        	 SetPipeAddressRequest request = rpc.getSerializer().decode(new ByteBufferInputStream(msgFrame.body), SetPipeAddressRequest.class);
        	 logger.log("task recv set pipe address request:%s", request.toString());
        	 task.setPipeAddress(request.getInPipeAddresses(), request.getOutPipeAddresses());
         }
    }
}
