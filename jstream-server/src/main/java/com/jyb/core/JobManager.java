package com.jyb.core;

import com.jyb.config.JstreamConfiguration;
import com.jyb.job.Job;
import com.jyb.job.JobImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.service.CompositeService;
import org.apache.hadoop.yarn.event.Dispatcher;

import javax.inject.Inject;

import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.*;

public class JobManager extends CompositeService {


    ConcurrentHashMap jobMap = new ConcurrentHashMap<String, Job>();

    @Inject
    public JobManager() {
        super("JobManager service");


    }

    @Override
    protected void serviceInit(Configuration conf) throws Exception {
        super.serviceInit(conf);
    }



    public void saveJob(JstreamConfiguration jstreamConfiguration){
        //获取jobid
        String jobid = "JSTREAM_JOB_999";
        //保存到数据库

        //存到jobMap中
//        Job job = new JobImpl();
//        jobMap.putIfAbsent(jobid,job);
//        job.saveJob(jstreamConfiguration);
    }

    public void listJobs(){

    }

    public void startJob(String jobId){

    }

    public void shutDownJob(String jobId){

    }
}
