package com.jyb.job;

public class FailJobEvent extends JobEvent {
    public FailJobEvent(String jobId) {
        super(jobId, JobEventType.JOB_FAIL);
    }
}
