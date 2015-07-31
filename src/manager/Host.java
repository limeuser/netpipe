package manager;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import cn.oasistech.agent.client.AgentSyncRpc;
import cn.oasistech.util.Address;
import cn.oasistech.util.IdGenerator;

public class Host {
    private String name;
    private String ip;
    
    private int id;
    private AgentSyncRpc rpc;
    private Map<String, Integer> services;
    
    private float cpuUseRate;
    private List<Cpu> cpus;
    private int totalMemory;
    private int freeMemory;
    private int maxNetIoQps;
    private int currNetIoQps;
    private int totalDiskSpace; // M
    private int freeDiskSpace; // M
    
    private List<Job> job = new ArrayList<Job>();
    
    private final static IdGenerator hostIdGenerator = new IdGenerator(1);
    
    public static Host connect(Address address) {
        Host host = new Host();
        host.rpc = new AgentSyncRpc();
        if (host.rpc.start(address) == false) {
            return null;
        }
        
        host.id = hostIdGenerator.getId();
        host.name = address.toString();
        host.services = new HashMap<String, Integer>();
        return host;
    }
}
