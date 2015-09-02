package mjoys.netpipe.pipe;

import mjoys.util.Cfg;

public class NetPipeCfg extends Cfg{
    public NetPipeCfg(String cfgFilePathInRoot, String defaultPropertyFileName) {
		super(cfgFilePathInRoot, defaultPropertyFileName);
	}
    
    public final static NetPipeCfg instance = new NetPipeCfg("cfg", "netpipe.cfg");

	public enum AgentTag {
		os,
    	netpipe_job,
    	netpipe_task,
    	netpipe_taskid,
    }
}
