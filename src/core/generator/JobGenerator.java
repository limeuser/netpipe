package core.generator;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import cn.oasistech.util.Logger;

import core.Job;
import core.Task;

public class JobGenerator {
    private static Map<String, JobDes> jobs = new HashMap<String, JobDes>();
    private final static Logger logger = new Logger().addPrinter(System.out);
    
    public final static void generate(Class<?> jobClass) {
        Job jobAnnotation = jobClass.getAnnotation(Job.class);
        if (jobAnnotation == null) {
            logger.log("not a job, can't find job annotation");
            return;
        }
        
        JobDes jobInfo = new JobDes();
        jobInfo.setJob(jobAnnotation);
        jobInfo.setJobClass(jobClass);
        
        // 查找所有task方法
        Method[] methods = jobClass.getDeclaredMethods();
        for (Method m : methods) {
            Task task = m.getAnnotation(Task.class);
            if (task != null) {
                TaskDes taskInfo = new TaskDes();
                taskInfo.setTask(task);
                taskInfo.setMethod(m);
                for (Type paramType : m.getGenericParameterTypes()) {
                    PipeDes pipeInfo = new PipeDes();
                    
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

        // 生成任务文件
        for (TaskDes task : jobInfo.getTasks()) {
            List<TaskDes> children = getChildren(task.name(), jobInfo.getTasks());
            children.add(task);
            
            Set<PipeDes> inPipes = new HashSet<PipeDes>();
            Set<PipeDes> outPipes = new HashSet<PipeDes>();
            mergePipeParams(children, inPipes, outPipes);
            
            String taskJavaFile = TaskTemplate.getTaskClassSourceCode(jobInfo, task, children, inPipes, outPipes);
            System.out.println(taskJavaFile);
        }
        
        // 生成数据流
        
        // 为管道指定地址，生成配置文件，程序启动时，读取配置文件来创建管道
        
        
        // 编译生成的java任务文件
        
        // 生成运行脚本
        
        // 打包任务和运行脚本并部署到集群
        
        // 运行任务
        
        // 运行时环境
        
        // 调度框架
        
        jobs.put(jobInfo.getJob().name(), jobInfo);
    }
    
    private static List<TaskDes> getChildren(String parent, List<TaskDes> tasks) {
        List<TaskDes> children = new ArrayList<TaskDes>();
        for (TaskDes task : tasks) {
            if (task.getTask().parent().equalsIgnoreCase(parent)) {
                children.add(task);
            }
        }
        return children;
    }
    
    private static void mergePipeParams(List<TaskDes> tasks, Set<PipeDes> inPipes, Set<PipeDes> outPipes) {
        for (TaskDes task : tasks) {
            inPipes.addAll(task.getInPipe());
            outPipes.addAll(task.getOutPipe());
        }
    }
    
    public Map<String, JobDes> getJobs() {
        return this.jobs;
    }
}
