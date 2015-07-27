package runner;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import core.PipeInfo;
import core.TaskInfo;

public class TaskTemplate {
    private static final String taskClassTemplate = 
            "public class Runner {\r\n" +
            "@InPipeVars" + 
            "@OutPipeVars" +
            "    private Context ctx = new Context;\r\n" +
            "    private @JobClassName job = new @JobClassName();\r\n" +
            "@TaskMethods\r\n" +
            "}";
    
    public static final String getTaskClass(String jobName, List<TaskInfo> tasks, Set<PipeInfo> inPipes, Set<PipeInfo> outPipes) {
        return taskClassTemplate
        .replaceAll("@JobClassName", jobName)
        .replaceAll("@InPipeVars", getInPipeVars(inPipes))
        .replaceAll("@OutPipeVars", getOutPipeVars(outPipes))
        .replaceAll("@TaskMethods", getAllTaskMethod(tasks));
    }
    
    private static final String getAllTaskMethod(List<TaskInfo> tasks) {
        StringBuilder str = new StringBuilder();
        for (TaskInfo task : tasks) {
            str.append(getTaskMethod(task));
            str.append("\r\n");
        }
        str.setLength(str.length() - "\r\n".length());
        return str.toString();
    }
    
    private static final String taskMethodTemplate = 
            "    public void @TaskMethodName() {\r\n" +
            "        Thread cmdThread = new Thread(new Runnable(){\r\n" + 
            "            @Override\r\n" + 
            "            public void run() {\r\n" +
            "                 @CallTaskMethod\r\n" + 
            "            }\r\n" +
            "        });\r\n" +
            "        cmdThread.start();\r\n" + 
            "    }";
    
    public static final String getTaskMethod(TaskInfo task) {
        return taskMethodTemplate.replaceAll("@TaskMethodName", task.getMethod().getName())
        .replaceAll("@CallTaskMethod", getCallTaskMethod(task));
    }
    
    public static final String getCallTaskMethod(TaskInfo task) {
        StringBuilder str = new StringBuilder();
        str.append("job.").append(task.getMethod().getName())
        .append("(");
        
        int inIndex = 0, outIndex = 0;
        for (Type paramType : task.getMethod().getParameterTypes()) {
            if (paramType.toString().contains("core.InPipe")) {
                str.append(task.getInPipe().get(inIndex++).name).append(", ");
            } else if (paramType.toString().contains("core.OutPipe")) {
                str.append(task.getOutPipe().get(outIndex++).name).append(", ");
            } else if (paramType.toString().contains("core.Context")) {
                str.append("ctx, ");
            } else {
                
            }
        }
        str.setLength(str.length() - ", ".length());
        str.append(");");
        
        return str.toString();
    }
    
    private static final String inPipeTemplate = "    private InPipe<@E> @InPipeName = new TcpInPipe<@E>();";
    private static final String outPipeTemplate = "    private OutPipe<@E> @OutPipeName = new TcpOutPipe<@E>();";
    
    public static final String getInPipeVars(Set<PipeInfo> inPipes) {
        StringBuilder str = new StringBuilder();
        for (PipeInfo p : inPipes) {
            str.append(inPipeTemplate.replaceAll("@E", p.elementType.toString().replace("$", ".").replace("class","").trim())
                                     .replaceAll("@InPipeName", p.name));
            str.append("\r\n");
        }

        return str.toString();
    }
    
    public static final String getOutPipeVars(Set<PipeInfo> outPipes) {
        StringBuilder str = new StringBuilder();
        for (PipeInfo p : outPipes) {
            str.append(outPipeTemplate.replaceAll("@E", p.elementType.toString().replace("$", ".").replace("class","").trim())
                                      .replaceAll("@OutPipeName", p.name));
            str.append("\r\n");
        }

        return str.toString();
    }
}