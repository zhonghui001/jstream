package com.jyb.job;

public class KillJobEvent extends JobEvent {

    String applicationId;
    public KillJobEvent(String jobId,String applicationId)
    {
        super(jobId, JobEventType.JOB_KILL);
        this.applicationId = applicationId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
