package com.jyb.job;

import org.apache.hadoop.yarn.event.AbstractEvent;


public class JobEvent extends AbstractEvent<JobEventType> {
    //jobid 额外的一些属性，用于传参
    private String jobId;


    public JobEvent(String jobId,JobEventType type){
        super(type);
        this.jobId = jobId;
    }



    public void setJobId(String jobId) {
        this.jobId = jobId;
    }



    public String getJobId() {
        return jobId;
    }

}
