package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import msg.TaskStatus;
import msg.MsgType;
import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.GetIdResponse;
import cn.oasistech.agent.client.AgentAsynRpc;
import cn.oasistech.util.Address;
import cn.oasistech.util.Cfg;
import cn.oasistech.util.ClassUtil;
import cn.oasistech.util.IdGenerator;
import cn.oasistech.util.Logger;
import cn.oasistech.util.Tag;
import core.Serializer;
import core.TLVFrame;
import core.generator.JobDes;
import core.generator.TaskDes;

public class Cluster {
    private Serializer serializer;
    private Timer reportTaskStatusTimer = new Timer();
    private List<Host> members = new ArrayList<Host>();
    
    private static int MaxLoad = 4;
    
    private final static Logger logger = new Logger().addPrinter(System.out);
    
    public void start() {
        // 解析配置文件，连接到所有工作节点
        
    	serializer = ClassUtil.newInstance(Cfg.getParserClassName());
    	
    	// 开始监控task状态
    	reportTaskStatusTimer.schedule(new UpdateTaskStatusTimerTask(), 0, TimeUnit.SECONDS.toMillis(1));
    }
    
    public void connectHost(String address) {
        Host host = Host.connect(Address.parse(address));
        if (host != null) {
            members.add(host);
        } else {
            logger.log("cant't connect address");
        }
    }
    
    public Host getHost(AgentAsynRpc rpc) {
        for (Host host : members) {
            if (rpc == host.getAsynRpc()) {
                return host;
            }
        }
        return null;
    }
    
    public void scheduleJob(JobDes jobInfo) {
        if (isTooBusyToSchedule()) {
            logger.log("warning: all node is busy, can't schedule new task");
            return;
        }
        
        // init running tasks
        int i = 0;
        int t = 0;
        List<RunningTask> runningTasks = new ArrayList<RunningTask>(jobInfo.getTasks().size());
        while (true) {
            if (members.get(i).getLoad() < MaxLoad) {
                runningTasks.add(members.get(i).initRunningTask(jobInfo.getTasks().get(t)));
                t++;
            }
            i = (i + 1) % members.size();
            if (t == jobInfo.getTasks().size()) {
                break;
            }
        }
        
        // link pipes
        for (RunningTask t1 : runningTasks) {
            for (InPipe in : t1.getInPipes().values()) {
                for (RunningTask t2 : runningTasks) {
                    if (t1 != t2) {
                        String inPipeName = in.getName();
                        OutPipe out = t2.getOutPipes().get(inPipeName);
                        if (out != null) {
                            in.setOut(out);
                            out.getIns().add(in);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public boolean isTooBusyToSchedule() {
        for (Host host : members) {
            if (host.getLoad() < MaxLoad) {
                return false;
            }
        }
        return true;
    }
    
    private Host getIdleHost() {
        double load = 0;
        Host idleHost = null;
        for (Host host : members) {
            if (host.getLoad() > load) {
                load = host.getLoad();
                idleHost = host;
            }
        }
        
        return idleHost;
    }
    
    public void sendTo(Host host, String service, byte[] body) {
    	GetIdResponse response = host.getSyncRpc().getId(new Tag(AgentProtocol.PublicTag.servicename.name(), service));
        for (int id : response.getIds()) {
           host.getAsynRpc().sendTo(id, body); 
        }
    }
    
    public void sentTo(String service, byte[] body, Host ...hosts) {
        for (Host host : hosts) {
            sendTo(host, service, body);
        }
    }
    
    public void sendToAll(String service, byte[] body) {
        for (Host host : members) {
        	sendTo(host, service, body);
        }
    }
    
    public void requestTaskStatus() {
        for (Host host : members) {
            host.requestTaskStatus();
        }
    }
    
    public class UpdateTaskStatusTimerTask extends TimerTask {
        @Override
        public void run() {
            requestTaskStatus();
        }
    }
    
    public Serializer getSerializer() {
        return this.serializer;
    }
}
