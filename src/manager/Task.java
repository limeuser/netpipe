package manager;

import java.util.List;

public class Task implements Comparable<Task> {
    private Job job;
    private String name;
    private List<RunningTask> runningTasks;
    
    public Task(Job job, String name) {
    	this.job = job;
    	this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null) {
    		return false;
    	}
    	if ((obj instanceof Task) == false) {
    		return false;
    	}
    	
    	Task task = (Task)obj;
    	if (job.equals(task.getJob()) && name.equals(task.getName())) {
    		return true;
    	}
    	
    	return false;
    }
    
    @Override 
    public int hashCode() {
    	return job.hashCode() + this.name.hashCode();
    }
    
    @Override 
    public int compareTo(Task task) {
    	if (this.job.equals(task.getJob())) {
    		return this.name.compareTo(task.getName());
    	}
    	return this.job.compareTo(task.getJob());
    }
    
    public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public List<RunningTask> getRunningTasks() {
		return runningTasks;
	}

	public void setRunningTasks(List<RunningTask> runningTasks) {
		this.runningTasks = runningTasks;
	}
}
