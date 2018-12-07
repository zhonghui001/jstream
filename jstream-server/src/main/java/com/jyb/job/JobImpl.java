package com.jyb.job;

import com.jyb.config.JstreamConfiguration;
import com.jyb.core.StatementParser;
import com.jyb.db.JobStreamService;
import com.jyb.job.vo.JobVo;
import com.jyb.job.vo.JobVoList;
import com.jyb.jstream.config.JstreamConf;
import com.jyb.yarn.SparkAppManager;
import io.airlift.log.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.service.CompositeService;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import scala.Tuple2;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class JobImpl extends CompositeService implements Job {
    private static final Logger log = Logger.get(JobImpl.class);

    JobStreamService jobStreamService;

    private JstreamConf jstreamConf;

    private SparkAppManager sparkAppManager;

    private ConcurrentHashMap<String, Tuple2<Dispatcher, JobStateMachine>> stateMachines = new ConcurrentHashMap<String, Tuple2<Dispatcher, JobStateMachine>>();


    private YarnClient yarnClient;

    private StatementParser parser;


    @Inject
    public JobImpl(JstreamConf jstreamConf,
                   JobStreamService jobStreamService, SparkAppManager sparkAppManager,
                   YarnClient yarnClient, StatementParser parser) {
        super("job ");
        this.jstreamConf = jstreamConf;
        this.jobStreamService = jobStreamService;
        this.sparkAppManager = sparkAppManager;
        this.yarnClient = yarnClient;
        this.parser = parser;
    }


    private void addStateMachine(String jobId, Dispatcher dispatcher, JobStateMachine jobStateMachine) {
        stateMachines.putIfAbsent(jobId, new Tuple2<>(dispatcher, jobStateMachine));
    }


    @Override
    protected void serviceInit(Configuration conf) throws Exception {


        super.serviceInit(conf);
    }

    @Override
    protected void serviceStart() throws Exception {
        super.serviceStart();
    }

    /**
     * 创建中央时间异步分发器和状态机
     *
     * @param jobId
     */
    private void createJobstateMachine(String jobId) {
        //创建中央异步事件分发器
        Dispatcher dispatcher = new AsyncDispatcher();
        //创建状态机
        JobStateMachine stateMachine = new JobStateMachine(yarnClient, dispatcher.getEventHandler(), jstreamConf, jobStreamService, sparkAppManager);
        //将中央异步事件分发器和状态机进行绑定
        dispatcher.register(JobEventType.class, stateMachine);
        //启动中央异步事件分发器
        ((AsyncDispatcher) dispatcher).init(new Configuration());
        ((AsyncDispatcher) dispatcher).start();

        addStateMachine(jobId, dispatcher, stateMachine);

    }

    private JobStateMachine getStateMachine(String jobId) {
        return stateMachines.get(jobId)._2;
    }

    private Dispatcher getDispatcher(String jobId) {

        if (stateMachines.get(jobId) == null) {
            JobVo job = jobStreamService.getJob(jobId);
            String jobState = job.getJobState();
            JobStateMachine.JobStateInternal jobStateInternal = JobStateMachine.JobStateInternal.valueOf(jobState);
            Dispatcher dispatcher = new AsyncDispatcher();
            JobStateMachine stateMachine =
                    new JobStateMachine(yarnClient, dispatcher.getEventHandler(), jstreamConf, jobStreamService, sparkAppManager, jobStateInternal);
            dispatcher.register(JobEventType.class, stateMachine);
            ((AsyncDispatcher) dispatcher).init(new Configuration());
            ((AsyncDispatcher) dispatcher).start();
            addStateMachine(jobId, dispatcher, stateMachine);
        }
        return stateMachines.get(jobId)._1;
    }

    @Override
    public String saveJob(JstreamConfiguration jstreamConfiguration,String sql) {
        //获取jobid
        //保存到数据库
        String jobid = jobStreamService.saveJob(createJobVo(jstreamConfiguration,sql));

        createJobstateMachine(jobid);

        //使用中央异步调度器 触发时间job_init
        JobEvent jobEvent = new InitJobEvent(jobid, jstreamConfiguration, jstreamConf);
        getDispatcher(jobid).getEventHandler().handle(jobEvent);

        return jobid;
    }

    @Override
    public String saveJob(String sql) {
        JstreamConfiguration configuration = this.parser.parser(sql);
        requireNonNull(configuration);
        return saveJob(configuration,sql);

    }



    private JobVo createJobVo(JstreamConfiguration jstreamConfiguration,String sql) {
        JobVo jobVo = new JobVo();
        jobVo.setConfiguration(jstreamConfiguration);
        jobVo.setSqlStr(sql);
        return jobVo;
    }


    @Override
    public JobVoList listJobs() {
        try {
            //查询

            List<JobVo> jobVoList = jobStreamService.list();

            for (JobVo jobVo : jobVoList) {
                String applicationId = jobVo.getApplicationId();
                if (StringUtils.isNotBlank(applicationId)) {
                    ApplicationId appId = ConverterUtils.toApplicationId(applicationId);
                    try {
                        ApplicationReport applicationReport = yarnClient.getApplicationReport(appId);
                        jobVo.setApplicationState(applicationReport.getYarnApplicationState().toString());
                    } catch (ApplicationNotFoundException ex) {
                        log.error("applicationId不存在了");
                        jobVo.setApplicationState("notFound");
                    }
                }
            }
            return new JobVoList(jobVoList);


        } catch (Exception ex) {
            ex.printStackTrace();
            return new JobVoList();
        }

    }

    @Override
    public void startJob(String jobId) {

        JstreamConfiguration conf = jobStreamService.getJob(jobId).getConfiguration();
        JobEvent runEvent = new StartJobEvent(jstreamConf, jobId, conf.getResouceConfig());
        getDispatcher(jobId).getEventHandler().handle(runEvent);
    }

    @Override
    public boolean killJob(String jobId) {
        try {
            JobVo job = jobStreamService.getJob(jobId);
            KillJobEvent killJobEvent = new KillJobEvent(jobId, job.getApplicationId());
            getDispatcher(jobId).getEventHandler().handle(killJobEvent);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean delJob(String jobId) {
        if (stateMachines.get(jobId) != null) {
            stateMachines.remove(jobId);
        }
        jobStreamService.delJob(jobId);
        return false;
    }

    @Override
    public void updateJob(JstreamConfiguration jstreamConfiguration, String jobId,String sql) {
        jobStreamService.updateJobConfiguration(jstreamConfiguration, jobId,sql);
    }

    @Override
    public void updateJob(String id, String sql) {
        JstreamConfiguration conf = this.parser.parser(sql);
        updateJob(conf,id,sql);
    }


    @Override
    public JobVo getJob(String jobId) {
        JobVo job = jobStreamService.getJob(jobId);
        return job;

    }


    @Override
    public long getProtocolVersion(String s, long l) throws IOException {
        return Job.versionID;
    }

    @Override
    public ProtocolSignature getProtocolSignature(String s, long l, int i) throws IOException {
        return new ProtocolSignature(Job.versionID, null);
    }


}
