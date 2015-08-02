package manager;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import core.AgentTag;
import core.Service;

import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.GetIdResponse;
import cn.oasistech.agent.GetIdTagResponse;
import cn.oasistech.agent.IdTag;
import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.agent.client.AgentSyncRpc;
import cn.oasistech.util.Address;
import cn.oasistech.util.IdGenerator;
import cn.oasistech.util.Logger;
import cn.oasistech.util.NumberUtil;
import cn.oasistech.util.StringUtil;
import cn.oasistech.util.Tag;

public class Host implements Comparable<Host> {
    private String name;
    private String ip;
    
    private int id;
    private AgentSyncRpc syncRpc;
    private AgentAsynRpc asynRpc;
    private Map<String, Integer> services;
    
    private float cpuUseRate;
    private List<Cpu> cpus;
    private int totalMemory;
    private int freeMemory;
    private int maxNetIoQps;
    private int currNetIoQps;
    private int totalDiskSpace; // M
    private int freeDiskSpace; // M
    
    private List<Job> jobs = new ArrayList<Job>();
    
    private final static Logger logger = new Logger().addPrinter(System.out);
    private final static IdGenerator hostIdGenerator = new IdGenerator(1);
    
    public static Host connect(Address address) {
        Host host = new Host();
        
        host.syncRpc = new AgentSyncRpc();
        if (host.syncRpc.start(address) == false) {
            return null;
        }
        
        host.asynRpc = new AgentAsynRpc();
        if (host.asynRpc.start(address, new ClusterMsgHandler()));
        
        host.id = hostIdGenerator.getId();
        host.name = address.toString();
        host.services = new HashMap<String, Integer>();
        return host;
    }
    
    public void getAllServices() {
    	// host service for performance
    	addService(Service.host.name());
    	
    	// runtime service for schedule tasks
    	addService(Service.runtime.name());
    	
    	// task service for control tasks
    	addAllTasks();
    }
    
    private void addAllTasks() {
    	List<Tag> tags = new ArrayList<Tag>();
    	tags.add(new Tag(AgentProtocol.PublicTag.servicename.name(), Service.dpipe_task.name()));
    	tags.add(new Tag(AgentTag.dpipe_job.name(), ""));
    	tags.add(new Tag(AgentTag.dpipe_task.name(), ""));
    	tags.add(new Tag(AgentTag.dpipe_id.name(), ""));
    	
    	GetIdTagResponse response = this.syncRpc.getIdTag(tags);
    	if (response != null && response.getError().equals(AgentProtocol.Error.Success.name())) {
    		for (IdTag idTag : response.getIdTags()) {
    			String jobName = "", taskName = "";
				int taskId = 0;
    			for (Tag tag : idTag.getTags()) {
    				if (tag.getKey().equals(AgentTag.dpipe_job.name()))
    					jobName = tag.getValue();
    				else if (tag.getKey().equals(AgentTag.dpipe_task.name())) {
    					taskName = tag.getValue();
    				} else if (tag.getKey().equals(AgentTag.dpipe_id.name())) {
    					taskId = NumberUtil.parseInt(tag.getValue());
    				}
    			}
    			
    			addTask(jobName, taskName, taskId);
    		}
    	}
    }
    
    private void addTask(String jobName, String taskName, int taskId) {
    	Job job = findJob(jobName);
    	if (job == null) {
    		job = addJob(jobName);
    	}
    	
		Task task = job.findTask(taskName, taskId);
		if (task == null) {
			job.addTask(taskName, taskId);
		} else {
			logger.log("find multi task: job=%s, taskName=%s, taskId=%d", jobName, taskName, taskId);
		}
    }
    
    public Job addJob(String name) {
    	Job job = new Job(this, name);
    	jobs.add(job);
    	return job;
    }
    
    public Job findJob(String name) {
    	for (Job job : jobs) {
    		if (job.getName().equals(name)) {
    			return job;
    		}
    	}
    	return null;
    }
    
    private void addService(String serviceName) {
    	int id = getService(serviceName);
    	if (id > 0) {
    		this.services.put(serviceName, id);
    	}
    }
    
    private int getService(String serviceName) {
    	List<Tag> tags = new ArrayList<Tag>();
    	tags.add(new Tag(AgentProtocol.PublicTag.servicename.name(), serviceName));
    	
    	GetIdResponse response = this.syncRpc.getId(tags);
    	if (response != null && response.getError().equals(AgentProtocol.Error.Success.name())) {
    		if (response.getIds().size() > 0) {
    			return response.getIds().get(0);
    		}
    	}
    	return 0;
    }
    
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null) {
    		return false;
    	}
    	if ((obj instanceof Host) == false) {
    		return false;
    	}
    	
    	Host host = (Host) obj;
    	if (StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(host.getName())) {
    		return name.equals(host.getName());
    	}
    	
    	return ip.equals(host.getIp());
    }
    
    @Override
    public int hashCode() {
    	if (StringUtil.isNotEmpty(name)) {
    		return name.hashCode();
    	}
    	
    	return this.ip.hashCode();
    }
    
    @Override
    public int compareTo(Host host) {
    	if (StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(host.getName())) {
    		return name.compareTo(host.getName());
    	}
    	return ip.compareTo(host.getIp());
    }
    
    public AgentSyncRpc getSyncRpc() {
    	return syncRpc;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public String getIp() {
    	return this.ip;
    }
}
