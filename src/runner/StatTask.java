package runner;

import java.util.TimerTask;
import msg.TaskStatus;
import msg.PipeStatus;
import core.InPipe;
import core.OutPipe;

public class StatTask extends TimerTask {
    private TaskRunner task;
    
    public StatTask(TaskRunner task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        // report task running status
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setJobName(task.getJobName());
        taskStatus.setTaskName(task.getTaskName());
        taskStatus.setTaskId(task.getTaskId());
        taskStatus.setWorkerCount(task.getWorks().size());
        
        for (InPipe<?> in : task.getIns()) {
            PipeStatus pipeStatus = new PipeStatus();
            pipeStatus.setName(in.name());
            pipeStatus.setSize(in.size());
            pipeStatus.setInQps(in.inQps());
            pipeStatus.setOutQps(in.outQps());
            pipeStatus.setCapacity(in.capacity());
            
            taskStatus.getPipeStatus().add(pipeStatus);
        }
        for (OutPipe<?> out : task.getOuts()) {
            PipeStatus pipeStatus = new PipeStatus();
            pipeStatus.setName(out.name());
            pipeStatus.setSize(out.size());
            pipeStatus.setInQps(out.inQps());
            pipeStatus.setOutQps(out.outQps());
            pipeStatus.setCapacity(out.capacity());
            
            taskStatus.getPipeStatus().add(pipeStatus);
        }
        
        this.task.getAgentRpc().sendTo(task.getServices().get(Config.DpipeManager), task.getAgentSerlizer().encode(taskStatus));
        
        for (OutPipe<?> out : task.getOuts()) {
            out.resetStat();
        }
        for (InPipe<?> in : task.getIns()) {
            in.resetStat();
        }
    }
}
