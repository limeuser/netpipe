package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.oasistech.agent.AgentProtocol;
import cn.oasistech.agent.GetIdResponse;
import cn.oasistech.agent.client.AgentSyncRpc;
import cn.oasistech.util.Address;
import cn.oasistech.util.IdGenerator;
import cn.oasistech.util.Logger;
import cn.oasistech.util.Tag;

public class Cluster {
    private List<Host> members = new ArrayList<Host>();
    private final static Logger logger = new Logger().addPrinter(System.out);
    
    public void start(String address) {
        Host host = Host.connect(Address.parse(address));
        if (host != null) {
            members.add(host);
        } else {
            logger.log("cant't connect address");
        }
    }
    
    public void sentTo(String service, byte[] body, Host ...hosts) {
        for (Host host : hosts) {
            GetIdResponse response = host.getRpc().getId(new Tag(AgentProtocol.PublicTag.servicename.name(), service));
            for (int id : response.getIds()) {
               host.getRpc().sendTo(id, body); 
            }
        }
    }
    
    public void sendToAll(String service, byte[] body) {
        Host[] hosts = new Host[members.size()];
        members.toArray(hosts);
        sentTo(service, body, hosts);
    }
}
