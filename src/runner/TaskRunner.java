package runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.util.Address;
import cn.oasistech.util.Tag;
import core.InPipe;
import core.OutPipe;
import core.Serializer;

public abstract class TaskRunner {
    private String jobName;
    private String taskName;
    private Timer statTimer;
    private List<Thread> works;
    private List<InPipe<?>> ins;
    private List<OutPipe<?>> outs;
    private Serializer agentSerlizer;
    private AgentAsynRpc agentRpc;
    private Map<String, Integer> services = new HashMap<String, Integer>();
    
    public TaskRunner(String jobName, String taskName) {
        this.jobName = jobName;
        this.taskName = taskName;
        
        this.statTimer = new Timer();
        this.statTimer.schedule(new StatTask(this), 0, 1000);
        this.agentRpc = new AgentAsynRpc();
        this.agentRpc.start(Address.parse("tcp://127.0.0.1:6953"), new TaskMsgHandler());
        
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag(Config.Job, this.jobName));
        tags.add(new Tag(Config.Task, this.taskName));
        
        this.agentRpc.setTag(tags);
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
        return agentRpc;
    }
    public void setAgentRpc(AgentAsynRpc agentRpc) {
        this.agentRpc = agentRpc;
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
