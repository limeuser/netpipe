package core;

import java.util.List;
import java.util.ArrayList;

public class JobInfo {
    private Job job;
    private Class<?> jobClass;
    private List<TaskInfo> tasks = new ArrayList<TaskInfo>();
    
    public Job getJob() {
        return job;
    }
    public void setJob(Job job) {
        this.job = job;
    }
    public List<TaskInfo> getTasks() {
        return tasks;
    }
    public void setTasks(List<TaskInfo> tasks) {
        this.tasks = tasks;
    }
    public Class<?> getJobClass() {
        return jobClass;
    }
    public void setJobClass(Class<?> jobClass) {
        this.jobClass = jobClass;
    }
}
