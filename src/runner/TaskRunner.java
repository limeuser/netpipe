package runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.GetIdResponse;
import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.agent.client.AgentSyncRpc;
import cn.oasistech.util.Address;
import cn.oasistech.util.Tag;
import core.InPipe;
import core.OutPipe;
import core.Serializer;
import cn.oasistech.util.Logger;

public abstract class TaskRunner {
    private String jobName;
    private String taskName;
    private Timer statTimer;
    private List<Thread> works;
    private List<InPipe<?>> ins;
    private List<OutPipe<?>> outs;
    private Serializer agentSerlizer;
    private AgentAsynRpc agentAsynRpc;
    private AgentSyncRpc agentSyncRpc;
    private Map<String, Integer> services = new HashMap<String, Integer>();
    
    private static final Logger logger = new Logger().addPrinter(System.out);
    
    public TaskRunner(String jobName, String taskName) {
        this.jobName = jobName;
        this.taskName = taskName;
        
        this.statTimer = new Timer();
        this.statTimer.schedule(new StatTask(this), 0, 1000);
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
        this.works.add(worker);
    }
    
    public void destoryWorker() {
        if (this.works.size() > 0) {
            Thread thread = works.remove(0);
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
        return works;
    }

    public void setWorks(List<Thread> works) {
        this.works = works;
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
}
