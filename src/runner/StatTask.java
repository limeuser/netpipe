package runner;

import java.util.TimerTask;

import cn.oasistech.util.Tag;
import core.InPipe;
import core.OutPipe;

public class StatTask extends TimerTask {
    private TaskRunner task;
    
    public StatTask(TaskRunner task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        // report stat inforamtion for backpress
        TaskStat stat = new TaskStat();
        stat.setJobName(task.getJobName());
        stat.setTaskName(task.getTaskName());
        stat.setWorkerCount(task.getWorks().size());
        
        for (InPipe<?> in : task.getIns()) {
            TaskStat.PipeStat pipeStat = stat.new PipeStat();
            pipeStat.setName(in.name());
            pipeStat.setSize(in.size());
            pipeStat.setInQps(in.inQps());
            pipeStat.setOutQps(in.outQps());
            pipeStat.setCapacity(in.capacity());
            stat.getPipeStat().add(pipeStat);
        }
        for (OutPipe<?> out : task.getOuts()) {
            TaskStat.PipeStat pipeStat = stat.new PipeStat();
            pipeStat.setName(out.name());
            pipeStat.setSize(out.size());
            pipeStat.setInQps(out.inQps());
            pipeStat.setOutQps(out.outQps());
            pipeStat.setCapacity(out.capacity());
            stat.getPipeStat().add(pipeStat);
        }
        
        this.task.getAgentRpc().sendTo(task.getServices().get(Config.DpipeManager), task.getAgentSerlizer().encode(stat));
        
        for (OutPipe<?> out : task.getOuts()) {
            out.resetStat();
        }
        for (InPipe<?> in : task.getIns()) {
            in.resetStat();
        }
    }
}
