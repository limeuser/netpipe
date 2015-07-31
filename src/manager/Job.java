package manager;

import java.util.ArrayList;
import java.util.List;

public class Job {
    private Host host;
    
    private String name;
    private List<Task> tasks = new ArrayList<Task>();
}
