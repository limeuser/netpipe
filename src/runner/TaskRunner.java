package runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msg.TaskStatus;
import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.GetIdResponse;
import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.agent.client.AgentSyncRpc;
import cn.oasistech.util.Address;
import cn.oasistech.util.Logger;
import cn.oasistech.util.Tag;
import core.InPipe;
import core.OutPipe;
import core.PipeStatus;
import core.Serializer;

public abstract class TaskRunner {
    private String jobName;
    private String taskName;
    private int taskId;
    private List<Thread> workers;
    private List<InPipe<?>> ins;
    private List<OutPipe<?>> outs;
    private Serializer agentSerlizer;
    private AgentAsynRpc agentAsynRpc;
    private AgentSyncRpc agentSyncRpc;
    private Map<String, Integer> services = new HashMap<String, Integer>();
    
    private static final Logger logger = new Logger().addPrinter(System.out);
    
    public TaskRunner(String jobName, String taskName, int taskId) {
        this.jobName = jobName;
        this.taskName = taskName;
        this.taskId = taskId;
        
        this.agentSyncRpc = new AgentSyncRpc();
        this.agentSyncRpc.start(Address.parse("tcp://127.0.0.1:6953"));
        
        this.agentAsynRpc = new AgentAsynRpc();
        this.agentAsynRpc.start(Address.parse("tcp://127.0.0.1:6953"), new TaskMsgHandler());
        
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag(Config.Job, this.jobName));
        tags.add(new Tag(Config.Task, this.taskName));
        tags.add(new Tag(AgentProtocol.PublicTag.clienttype.name(), AgentProtocol.ClientType.asyn.name()));
        this.agentAsynRpc.setTag(tags);
        
        connectTaskManager();
    }
    
    private void connectTaskManager() {
        while (true) {
            GetIdResponse response = this.agentSyncRpc.getId(new Tag(AgentProtocol.PublicTag.servicename.name(), Config.DpipeManager));
            if (response.getIds().size() == 1) {
                this.services.put(Config.DpipeManager, response.getIds().get(0));
                break;
            } else {
                logger.log("can't get manager");
            }
        }
    }
    
    protected abstract void init();
    protected abstract void runTask();
    
    public void createWorker() {
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                runTask();
            }
        });
        
        worker.start();
        this.workers.add(worker);
    }
    
    public void destoryWorker() {
        if (this.workers.size() > 0) {
            Thread thread = workers.remove(0);
            thread.stop();
        }
    }
    
    public void setMaxQps(String outPipeName, String peerAddress, int qps) {
        for (OutPipe<?> out : outs) {
            if (out.name().equals(outPipeName)) {
                out.setMaxQps(Address.parse(peerAddress), qps);
            }
        }
    }
    
    public void switchOutPipe(String inPipeName, String outPipeAddress) {
        for (InPipe<?> in : ins) {
            if (in.name().equals(inPipeName)) {
                in.switchOutPipe(outPipeAddress);
                break;
            }
        }
    }
    
    // get task running status
    public TaskStatus getStatus() {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setTaskId(taskId);
        taskStatus.setWorkerCount(workers.size());
        
        for (InPipe<?> in : ins) {
            taskStatus.getPipeStatus().put(in.name(), in.getStatus());
        }
        for (OutPipe<?> out : outs) {
            PipeStatus pipeStatus = new PipeStatus();
 
        }
        
        return taskStatus;
    }
    
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public List<InPipe<?>> getIns() {
        return ins;
    }
    public void setIns(List<InPipe<?>> ins) {
        this.ins = ins;
    }
    public List<OutPipe<?>> getOuts() {
        return outs;
    }
    public void setOuts(List<OutPipe<?>> outs) {
        this.outs = outs;
    }
    public void addInPipe(InPipe<?> in) {
        this.ins.add(in);
    }
    public void addOutPipe(OutPipe<?> out) {
        this.outs.add(out);
    }
    public AgentAsynRpc getAgentRpc() {
        return agentAsynRpc;
    }
    public void setAgentRpc(AgentAsynRpc agentRpc) {
        this.agentAsynRpc = agentRpc;
    }

    public List<Thread> getWorks() {
        return workers;
    }

    public void setWorks(List<Thread> works) {
        this.workers = works;
    }

    public Map<String, Integer> getServices() {
        return services;
    }

    public void setServices(Map<String, Integer> services) {
        this.services = services;
    }

    public Serializer getAgentSerlizer() {
        return agentSerlizer;
    }

    public void setAgentSerlizer(Serializer agentSerlizer) {
        this.agentSerlizer = agentSerlizer;
    }
    public int getTaskId() {
    	return this.taskId;
    }
}
