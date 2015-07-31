package manager;

import java.util.ArrayList;
import java.util.List;

public class OutPipe {
    private Task task;
    
    private String name;
    private int size;
    private int capacity;
    private int inQps;
    private int outQps;
    
    private List<InPipe> ins = new ArrayList<InPipe>();
}
