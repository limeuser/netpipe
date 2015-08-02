package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import core.Serializer;

import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.GetIdResponse;
import cn.oasistech.agent.client.AgentSyncRpc;
import cn.oasistech.util.Address;
import cn.oasistech.util.Cfg;
import cn.oasistech.util.ClassUtil;
import cn.oasistech.util.IdGenerator;
import cn.oasistech.util.Logger;
import cn.oasistech.util.Tag;

public class Cluster {
    private List<Host> members = new ArrayList<Host>();
    private Timer reportTaskStatusTimer = new Timer();
    private Serializer serializer;
    
    private final static Logger logger = new Logger().addPrinter(System.out);
    
    public void start() {
    	serializer = ClassUtil.newInstance(Cfg.getParserClassName());
    	reportTaskStatusTimer.schedule(new, time)
    }
    
    public void connectHost(String address) {
        Host host = Host.connect(Address.parse(address));
        if (host != null) {
            members.add(host);
        } else {
            logger.log("cant't connect address");
        }
    }
    
    public void sendTo(Host host, String service, byte[] body) {
    	GetIdResponse response = host.getSyncRpc().getId(new Tag(AgentProtocol.PublicTag.servicename.name(), service));
        for (int id : response.getIds()) {
           host.getSyncRpc().sendTo(id, body); 
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
    
    public class GetTaskStatusTimerTask extends TimerTask {
		@Override
		public void run() {
			sendToAll();
		}
    }
}
