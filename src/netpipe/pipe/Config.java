package netpipe.pipe;

import mjoys.io.Serializer;
import mjoys.util.ClassUtil;
import cn.oasistech.util.Cfg;

public class Config {
    public final static String Dpipe = "dpipe";
    public final static String DpipeManager = "dpipe-manager";
    
    public final static String Task = "task";
    public final static String Job = "job";
    
    private final static Serializer serializer = ClassUtil.newInstance(Cfg.getParserClassName());
    public final static Serializer getSerializer() {
        return serializer;
    }
}
