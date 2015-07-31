package runner;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import core.*;
import cn.oasistech.util.Logger;

public class JobRunner {
    private final static Logger logger = new Logger().addPrinter(System.out);
    
    public final static void run(Class<?> jobClass) {
        Job jobAnnotation = jobClass.getAnnotation(Job.class);
        if (jobAnnotation == null) {
            logger.log("not a job, can't find job annotation");
            return;
        }
        
        JobInfo jobInfo = new JobInfo();
        jobInfo.setJob(jobAnnotation);
        jobInfo.setJobClass(jobClass);
        
        // 查找所有task方法
        Method[] methods = jobClass.getDeclaredMethods();
        for (Method m : methods) {
            Task task = m.getAnnotation(Task.class);
            if (task != null) {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTask(task);
                taskInfo.setMethod(m);
                for (Type paramType : m.getGenericParameterTypes()) {
                    PipeInfo pipeInfo = new PipeInfo();
                    
                    if (paramType instanceof ParameterizedType) {
                        Type genericParamType = ((ParameterizedType) paramType).getActualTypeArguments()[0];
                        pipeInfo.elementType = genericParamType;
                    }
                    
                    if (paramType.toString().contains("core.InPipe")) {
                        taskInfo.getInPipe().add(pipeInfo);
                    } else if (paramType.toString().contains("core.OutPipe")) {
                        taskInfo.getOutPipe().add(pipeInfo);
                    } else if (paramType.toString().contains("core.Context")) {
                        
                    } else {
                        logger.log("error: bad type of task method");
                    }
                }
                
                if (taskInfo.getInPipe().size() != taskInfo.getTask().in().length ||
                    taskInfo.getOutPipe().size() != taskInfo.getTask().out().length) {
                    logger.log("pipe number not same");
                } else {
                    for (int i = 0; i < taskInfo.getTask().in().length; i++) {
                        taskInfo.getInPipe().get(i).name = taskInfo.getTask().in()[i];
                    }
                    for (int i = 0; i < taskInfo.getTask().out().length; i++) {
                        taskInfo.getOutPipe().get(i).name = taskInfo.getTask().out()[i];
                    }
                    jobInfo.getTasks().add(taskInfo);
                }
            }
        }
        
        if (jobInfo.getTasks().isEmpty()) {
            logger.log("no task");
            return;
        }
        
        // 生成数据流
        
        // 为管道指定地址，生成配置文件，程序启动时，读取配置文件来创建管道
        
        // 生成任务文件
        for (TaskInfo task : jobInfo.getTasks()) {
            List<TaskInfo> children = getChildren(task.name(), jobInfo.getTasks());
            children.add(task);
            
            Set<PipeInfo> inPipes = new HashSet<PipeInfo>();
            Set<PipeInfo> outPipes = new HashSet<PipeInfo>();
            mergePipeParams(children, inPipes, outPipes);
            
            String taskJavaFile = TaskTemplate.getTaskClassSourceCode(jobInfo, task, children, inPipes, outPipes);
            System.out.println(taskJavaFile);
        }
        
        // 编译生成的java任务文件
        
        // 生成运行脚本
        
        // 打包任务和运行脚本并部署到集群
        
        // 运行任务
        
        // 运行时环境
        
        // 调度框架
    }
    
    private static List<TaskInfo> getChildren(String parent, List<TaskInfo> tasks) {
        List<TaskInfo> children = new ArrayList<TaskInfo>();
        for (TaskInfo task : tasks) {
            if (task.getTask().parent().equalsIgnoreCase(parent)) {
                children.add(task);
            }
        }
        return children;
    }
    
    private static void mergePipeParams(List<TaskInfo> tasks, Set<PipeInfo> inPipes, Set<PipeInfo> outPipes) {
        for (TaskInfo task : tasks) {
            inPipes.addAll(task.getInPipe());
            outPipes.addAll(task.getOutPipe());
        }
    }
}
