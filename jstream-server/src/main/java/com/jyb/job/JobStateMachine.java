package com.jyb.job;

import com.jyb.db.JobStreamService;
import com.jyb.job.vo.JobConf;
import com.jyb.jstream.config.JstreamConf;
import com.jyb.shell.Shell;
import com.jyb.yarn.SparkAppManager;
import io.airlift.log.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.state.InvalidStateTransitonException;
import org.apache.hadoop.yarn.state.SingleArcTransition;
import org.apache.hadoop.yarn.state.StateMachine;
import org.apache.hadoop.yarn.state.StateMachineFactory;
import org.apache.hadoop.yarn.util.ConverterUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static java.util.Objects.*;


public class JobStateMachine implements EventHandler<JobEvent> {
    private static final Logger log = Logger.get(JobStateMachine.class);

    private EventHandler eventHandler;
    private final Lock writeLock;
    private final Lock readLock;
    private final StateMachine<JobStateInternal, JobEventType, JobEvent> stateMachine;
    private final JstreamConf jstreamConf;

    private final JobStreamService jobStreamService;
    private final SparkAppManager sparkAppManager;
    private final YarnClient yarnClient;

    private static InitTransition INIT_TRANSITION = new InitTransition();

    public JobStateMachine(YarnClient yarnClient,EventHandler eventHandler, JstreamConf jstreamConf,
                           JobStreamService jobStreamService, SparkAppManager sparkAppManager) {
        this(yarnClient,eventHandler,jstreamConf,jobStreamService,sparkAppManager,JobStateInternal.NEW);
    }


    public JobStateMachine(YarnClient yarnClient,EventHandler eventHandler, JstreamConf jstreamConf,
                           JobStreamService jobStreamService,SparkAppManager sparkAppManager,JobStateInternal jobState) {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        this.yarnClient = yarnClient;
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
        this.eventHandler = eventHandler;
        this.jstreamConf = jstreamConf;
        this.jobStreamService = jobStreamService;
        this.sparkAppManager = sparkAppManager;
        stateMachine = stateMachineFactory.make(this,jobState);
    }

    //定义状态机 泛型为 statemachine,state enum,e
    protected static final StateMachineFactory<JobStateMachine, JobStateInternal, JobEventType, JobEvent>
            stateMachineFactory = new StateMachineFactory<JobStateMachine, JobStateInternal, JobEventType, JobEvent>(JobStateInternal.NEW)

            //job前状态，job后状态，事件，hook函数（在hook函数中触发下一个事件）
            .addTransition(JobStateInternal.NEW, JobStateInternal.INITED, JobEventType.JOB_INIT, new InitTransition())

            .addTransition(JobStateInternal.INITED, JobStateInternal.RUNNING, JobEventType.JOB_RUN, new StartTransition())

            .addTransition(JobStateInternal.RUNNING,
                    JobStateInternal.FAILED,
                    JobEventType.JOB_FAIL, new FailTransition())

            .addTransition(JobStateInternal.RUNNING, JobStateInternal.KILLED, JobEventType.JOB_KILL, new KillTransition())

            .addTransition(JobStateInternal.KILLED,JobStateInternal.RUNNING,JobEventType.JOB_RUN,new StartTransition())
            .addTransition(JobStateInternal.FAILED,JobStateInternal.RUNNING,JobEventType.JOB_RUN,new StartTransition())

            .installTopology();

    protected StateMachine<JobStateInternal, JobEventType, JobEvent> getStateMachine() {
        return this.stateMachine;
    }

    public static class InitTransition implements SingleArcTransition<JobStateMachine, JobEvent> {

        @Override
        public void transition(JobStateMachine job, JobEvent event) {


            InitJobEvent initJobEvent = (InitJobEvent) event;
            //回写mysql为inited
            job.jobStreamService.updateJobState(initJobEvent.getJobId(), JobStateInternal.INITED);
        }


    }

    public static class FailTransition implements SingleArcTransition<JobStateMachine, JobEvent> {

        @Override
        public void transition(JobStateMachine job, JobEvent jobEvent) {
            //修改mysql中job的状态为failed
            job.jobStreamService.updateJobState(jobEvent.getJobId(), JobStateInternal.FAILED);
        }
    }

    public static class KillTransition implements SingleArcTransition<JobStateMachine, JobEvent> {

        @Override
        public void transition(JobStateMachine job, JobEvent jobEvent) {
            job.jobStreamService.updateJobState(jobEvent.getJobId(), JobStateInternal.KILLED);
            KillJobEvent killJobEvent = (KillJobEvent) jobEvent;
            ApplicationId applicationId = ConverterUtils.toApplicationId(killJobEvent.getApplicationId());
            try {
                job.yarnClient.killApplication(applicationId);
            } catch (YarnException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * start的钩子函数
     */
    public static class StartTransition implements SingleArcTransition<JobStateMachine, JobEvent> {

        @Override
        public void transition(JobStateMachine job, JobEvent event) {
            //1.获取到配置文件的位置
            StartJobEvent startJobEvent = (StartJobEvent) event;

            //spark jar包路径
            String sparkJarPath = getSparkJarPath(startJobEvent);
            //ext jar包路径
            String extPathPath = getExtJarPath(startJobEvent);
            //2.提交jar包和配置文件，推送到yarn集群中
            JobConf jobConf = new JobConf(sparkJarPath+","+extPathPath,
                    startJobEvent.getJobId());
            try {
                System.out.println(jobConf.getCommandLine());
                new Thread(()->{
                    try{
                        String dirPath = requireNonNull(System.getenv("JSTREAM_HOME"),"JSTREAM_HOME未被设置")+"/logs/"+jobConf.getJobId();
                        File dir = new File(dirPath);
                        if (!dir.exists())
                            dir.mkdirs();

                        Shell shell = new Shell(dir.getAbsolutePath()+"/out.log",dir.getAbsolutePath()+"/err.log");
                        shell.execute(jobConf.getCommandLine());

                    }catch (Exception ex){
                        log.error("任务出现失败，"+jobConf.getCommandLine());
                        log.error(ex);
                        FailJobEvent failJobEvent = new FailJobEvent(jobConf.getJobId());
                        job.eventHandler.handle(failJobEvent);
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
            //更新数据库状态
            job.jobStreamService.updateJobState(startJobEvent.getJobId(),JobStateInternal.RUNNING);
            job.jobStreamService.cleanJobApplicationId(startJobEvent.getJobId());

        }

        private String getExtJarPath(StartJobEvent startJobEvent){
            //SPARK JAR包的位置路径
            String jarPathDir = startJobEvent.getJstreamConf().getJstreamHome() + "/lib";

            String jarPath = Arrays.stream(new File(jarPathDir).listFiles())
                    .map(file -> file.getAbsolutePath()).reduce((s, s2) -> s + "," + s2).get();
            return jarPath;
        }

        private String getSparkJarPath(StartJobEvent startJobEvent){
            //SPARK JAR包的位置路径
            String jarPathDir = startJobEvent.getJstreamConf().getJstreamHome() + "/lib";
            log.info("jar包路径："+jarPathDir);
            String jarPath = Arrays.stream(new File(jarPathDir).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (StringUtils.contains(name, "jstream"))
                        return true;
                    else
                        return false;
                }
            })).map(file -> file.getAbsolutePath()).reduce((s, s2) -> s + "," + s2).get();
            return jarPath;
        }

    }

    public static class SetupCompletedTransition implements SingleArcTransition<JobStateMachine, JobEvent> {

        @Override
        public void transition(JobStateMachine job, JobEvent event) {
//            System.out.println("Receiving event " + event);
//            job.eventHandler.handle(new JobEvent(job.getJobID(),JobEventType.JOB_COMPLETED)) ;
        }

    }


    @Override
    public void handle(JobEvent event) {
        try {
            writeLock.lock();
            JobStateInternal oldState = getInternalState();

            try {
                getStateMachine().doTransition(event.getType(), event);
            } catch (InvalidStateTransitonException e) {
                System.out.println("Can't handle this event at current state");
            }
            if (oldState != getInternalState()) {
                System.out.println("Job Transitioned from " + oldState + " to " + getInternalState());
            }
        } finally {
            writeLock.unlock();
        }
    }


    public JobStateInternal getInternalState() {
        readLock.lock();
        try {
            return getStateMachine().getCurrentState();
        } finally {
            readLock.unlock();
        }
    }


    public enum JobStateInternal {//作业内部状态
        NEW,
        INITED,
        RUNNING,
        KILLED,
        FAILED
    }

    public JobStateInternal getState() {
        return stateMachine.getCurrentState();
    }

}