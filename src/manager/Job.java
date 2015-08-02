package manager;

import java.util.ArrayList;
import java.util.List;

public class Job implements Comparable<Job> {
    private Host host;
    
    private String name;
    private List<Task> tasks = new ArrayList<Task>();
    
    public Job(Host host, String name) {
    	this.host = host;
    	this.name = name;
    }
    
    public Task addTask(String name) {
    	Task task = new Task(this, name);
    	tasks.add(task);
    	return task;
    }
    
    public Task findTask(String name) {
    	for (Task t : tasks) {
    		if (t.getName().equals(name)) {
    			return t;
    		}
    	}
    	return null;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null) {
    		return false;
    	}
    	if ((obj instanceof Job) == false) {
    		return false;
    	}
    	
    	Job job = (Job)obj;
    	if (host.equals(job.getHost()) && name.equals(job.getName())) {
    		return true;
    	}
    	return false;
    }
    
    @Override
    public int hashCode() {
    	return host.hashCode() + name.hashCode();
    }
    
    @Override
    public int compareTo(Job job) {
    	if (host.equals(job.getHost())) {
    		return this.name.compareTo(job.getName());
    	}
    	return host.compareTo(job.getHost());
    }
    
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
}
