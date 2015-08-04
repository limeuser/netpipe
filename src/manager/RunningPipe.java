package manager;

import java.util.List;

import core.PipeStatus;

public class RunningPipe {
    private String name;
    private PipeStatus status;
    private List<RunningPipe> peers;
}
