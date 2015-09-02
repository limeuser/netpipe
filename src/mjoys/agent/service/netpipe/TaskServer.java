package mjoys.agent.service.netpipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mjoys.agent.Agent;
import mjoys.agent.GetIdResponse;
import mjoys.agent.client.AgentAsynRpc;
import mjoys.agent.client.AgentSyncRpc;
import mjoys.agent.util.AgentCfg;
import mjoys.agent.util.Tag;
import mjoys.netpipe.pipe.InPipe;
import mjoys.netpipe.pipe.NetPipeCfg;
import mjoys.netpipe.pipe.OutPipe;
import mjoys.netpipe.pipe.TaskStatus;
import mjoys.util.Address;
import mjoys.util.Logger;

public abstract class TaskServer {
	private String jobName;
    private String taskName;
    private int taskId;
    private List<Thread> workers;
    private List<InPipe<?>> ins;
    private List<OutPipe<?>> outs;
    private AgentAsynRpc agentAsynRpc;
    private AgentSyncRpc agentSyncRpc;
    private Map<String, Integer> services;
    
    private static final Logger logger = new Logger().addPrinter(System.out);
    
    public TaskServer(String jobName, String taskName, int taskId) {
        this.jobName = jobName;
        this.taskName = taskName;
        this.taskId = taskId;
        this.workers = new ArrayList<Thread>();
        this.ins = new ArrayList<InPipe<?>>();
        this.outs = new ArrayList<OutPipe<?>>();
        this.services = new HashMap<String, Integer>();
        
        this.agentSyncRpc = new AgentSyncRpc();
        this.agentSyncRpc.start(Address.parse(AgentCfg.instance.getServerAddress()));
        
        this.agentAsynRpc = new AgentAsynRpc();
        this.agentAsynRpc.start(Address.parse(AgentCfg.instance.getServerAddress()), new TaskMsgHandler(this));
        
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag(NetPipeCfg.AgentTag.netpipe_job.name(), this.jobName));
        tags.add(new Tag(NetPipeCfg.AgentTag.netpipe_task.name(), this.taskName));
        tags.add(new Tag(NetPipeCfg.AgentTag.netpipe_taskid.name(), String.valueOf(this.taskId)));
        tags.add(new Tag(Agent.PublicTag.clienttype.name(), Agent.ClientType.asyn.name()));
        this.agentAsynRpc.setTag(tags);
        
        connectService(NetPipeCfg.AgentTag.netpipe_manager.name());
    }
    
    private void connectService(String name) {
        while (true) {
            GetIdResponse response = this.agentSyncRpc.getId(new Tag(Agent.PublicTag.servicename.name(), name));
            if (response.getIds().size() == 1) {
                this.services.put(name, response.getIds().get(0));
                break;
            } else {
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                logger.log("can't find service:" + name);
            }
        }
    }
    
    public abstract void runTask();
    
    public void connectOutPipe(String inPipeName, String outPipeAddress) {
    	for (InPipe<?> in : ins) {
    		if (in.name().equals(inPipeName)) {
    			in.connect(outPipeAddress);
    		}
    	}
    }
    
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
            taskStatus.getPipeStatus().put(out.name(), out.getStatus());
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
    
    public int getTaskId() {
    	return this.taskId;
    }
}
