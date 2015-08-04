package manager;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import msg.MsgType;
import msg.TaskStatus;
import core.AgentTag;
import core.Serializer;
import core.Service;
import core.TLVFrame;
import core.generator.PipeDes;
import core.generator.TaskDes;
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
    
    private AgentSyncRpc syncRpc;
    private AgentAsynRpc asynRpc;
    private Map<String, Integer> services;
    
    private Serializer serializer;
    
    private float cpuUseRate;
    private List<Cpu> cpus;
    private int totalMemory;
    private int freeMemory;
    private int maxNetIoQps;
    private int currNetIoQps;
    private int totalDiskSpace; // M
    private int freeDiskSpace; // M
    
    private int idleTcpPortIndex = 0;
    private final static int StartTcpPort = 8000;
    private boolean[] idleTcpPorts = new boolean[2000];
    
    private double load;
    
    private final static IdGenerator taskIdGenerator = new IdGenerator(1);
    private Map<Integer, RunningTask> runningTasks = new HashMap<Integer, RunningTask>();
    
    private final static Logger logger = new Logger().addPrinter(System.out);
    
    public static Host connect(Address address) {
        Host host = new Host();
        
        host.syncRpc = new AgentSyncRpc();
        if (host.syncRpc.start(address) == false) {
            return null;
        }
        
        host.asynRpc = new AgentAsynRpc();
        if (host.asynRpc.start(address, new TaskMsgHandler(host)));
        
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
    	listenRunningTask();
    }
    
    private void listenRunningTask() {
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag(AgentProtocol.PublicTag.servicename.name(), Service.dpipe_task.name()));
        this.asynRpc.listenConnection(tags);
    }
    
    public void runningTaskConnected(int taskId, int agentId) {
        RunningTask task = runningTasks.get(taskId);
        if (task == null) {
            logger.log("running task connected, but can't find the task: id=%d", taskId);
            return;
        }
        
        task.setAgentId(agentId);
        task.setRunningStatus(TaskRunningStatus.Connected);
    }

    public void updateRunningTaskStatus(TaskStatus status) {
        RunningTask task = runningTasks.get(status.getTaskId());
        if (task == null) {
            logger.log("can't find running task when upate task status: id=%d", status.getTaskId());
            return;
        }
        
        task.setWorkerCount(status.getWorkerCount());
        task.setRunningStatus(TaskRunningStatus.Running);
    }
    
    public void requestTaskStatus() {
        for (RunningTask task : runningTasks.values()) {
            TLVFrame frame = new TLVFrame();
            frame.setType(MsgType.ReportStatus.ordinal());
            frame.setLength(0);
            this.asynRpc.sendTo(task.getAgentId(), serializer.encode(frame));
        }
    }
    
    public RunningTask initRunningTask(TaskDes taskInfo) {
        RunningTask runningTask = RunningTask.newRunningTask(taskInfo, taskIdGenerator.getId(), this);

        for (PipeDes p : taskInfo.getInPipe()) {
            runningTask.getInPipes().put(p.name, InPipe.newInPipe(runningTask, p.name));
        }
        for (PipeDes p : taskInfo.getOutPipe()) {
            runningTask.getOutPipes().put(p.name, OutPipe.newOutPipe(runningTask, p.name, getAddress()));
        }
        
        return runningTask;
    }

    private boolean addService(String serviceName) {
    	int id = getAndListenService(serviceName);
    	if (id > 0) {
    		this.services.put(serviceName, id);
    		return true;
    	}
    	return false;
    }
    
    private int getAndListenService(String serviceName) {
    	List<Tag> tags = new ArrayList<Tag>();
    	tags.add(new Tag(AgentProtocol.PublicTag.servicename.name(), serviceName));
    	
    	GetIdResponse response = this.syncRpc.getId(tags);
    	if (response != null && response.getError().equals(AgentProtocol.Error.Success.name())) {
    		if (response.getIds().size() > 0) {
    		    this.asynRpc.listenConnection(tags);
    			return response.getIds().get(0);
    		}
    	}
    	return 0;
    }
    
    public int getIdleTcpPort() {
        for (int i = idleTcpPortIndex; i < idleTcpPorts.length; i++) {
            if (idleTcpPorts[i] == true) {
                idleTcpPorts[i] = false;
                i++;
                return i + StartTcpPort;
            }
        }
        
        return 0;
    }
    
    public void putIdleTcpPort(int port) {
        int index = port - StartTcpPort;
        if (index < 0 || index > idleTcpPorts.length) {
            logger.log("free pipe tcp port is not invalid");
            return;
        }
        
        idleTcpPorts[index] = true;
    }
    
    public Address getAddress() {
        return Address.parse("tcp://" + this.ip + ":" + getIdleTcpPort());
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
    
    public double getLoad() {
        return this.load;
    }
    
    public AgentSyncRpc getSyncRpc() {
    	return syncRpc;
    }
    
    public AgentAsynRpc getAsynRpc() {
        return asynRpc;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public String getIp() {
    	return this.ip;
    }
    
    public Map<Integer, RunningTask> getRunningTasks() {
        return this.runningTasks;
    }
}
