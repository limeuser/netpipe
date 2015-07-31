package manager;

import java.util.List;
import java.util.ArrayList;

public class Task {
    private Job job;
    
    private String name;
    private int id;
    private int workerCount;
    
    private List<InPipe> ins = new ArrayList<InPipe>();
    private List<OutPipe> outs = new ArrayList<OutPipe>();
}
