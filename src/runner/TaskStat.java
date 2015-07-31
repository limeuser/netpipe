package runner;

import java.util.ArrayList;
import java.util.List;

public class TaskStat {
    private String jobName;
    private String taskName;
    private int workerCount;
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
    
    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public class PipeStat {
        private String name;
        private int size;
        private int capacity;
        private int inQps;
        private int outQps;
        
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
        public int getInQps() {
            return inQps;
        }
        public void setInQps(int inQps) {
            this.inQps = inQps;
        }
        public int getOutQps() {
            return outQps;
        }
        public void setOutQps(int outQps) {
            this.outQps = outQps;
        }
    }
}
