package runner;

import java.util.TimerTask;

import core.OutPipe;

public class StatTask extends TimerTask {
    private TaskRunner task;
    public StatTask(TaskRunner task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        for (OutPipe<?> out : task.getOuts()) {
            out.
        }
    }
}
