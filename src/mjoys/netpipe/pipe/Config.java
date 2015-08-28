package mjoys.netpipe.pipe;

import mjoys.agent.util.AgentCfg;
import mjoys.io.Serializer;
import mjoys.util.ClassUtil;

public class Config {
    public final static String Dpipe = "dpipe";
    public final static String DpipeManager = "dpipe-manager";
    
    public final static String Task = "task";
    public final static String Job = "job";
    
    private final static Serializer serializer = ClassUtil.newInstance(AgentCfg.instance.getSerializerClassName());
    public final static Serializer getSerializer() {
        return serializer;
    }
}
