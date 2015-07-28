package runner;

import java.util.List;
import java.util.Timer;

import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.util.Address;
import core.InPipe;
import core.OutPipe;

public abstract class TaskRunner {
    private String jobName;
    private String taskName;
    private Timer statTimer;
    private List<Thread> works;
    private List<InPipe<?>> ins;
    private List<OutPipe<?>> outs;
    private AgentAsynRpc agentRpc;
    
    public TaskRunner(String jobName, String taskName) {
        this.jobName = jobName;
        this.taskName = taskName;
        
        this.statTimer = new Timer();
        this.statTimer.schedule();
        this.agentRpc = new AgentAsynRpc();
        this.agentRpc.start(Address.parse("tcp://127.0.0.1:6953"), new TaskMsgHandler());
    }
    
    protected abstract void init();
    protected abstract void run();
    
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
    public void addWork(Thread thread) {
        this.works.add(thread);
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
}
