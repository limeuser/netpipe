package runner;

import java.util.ArrayList;
import java.util.List;

public class TaskStat {
    private String jobName;
    private String taskName;
    private List<PipeStat> pipeStat = new ArrayList<PipeStat>();
    
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

    public List<PipeStat> getPipeStat() {
        return pipeStat;
    }

    public void setPipeStat(List<PipeStat> pipeStat) {
        this.pipeStat = pipeStat;
    }
    
    public class PipeStat {
        private String name;
        private int size;
        private int capacity;
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getSize() {
            return size;
        }
        public void setSize(int size) {
            this.size = size;
        }
        public int getCapacity() {
            return capacity;
        }
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }
    }
}
