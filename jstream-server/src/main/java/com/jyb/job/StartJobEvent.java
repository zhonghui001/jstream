package com.jyb.job;

import com.jyb.jstream.config.JstreamConf;

public class StartJobEvent extends JobEvent {

    private JstreamConf jstreamConf;

    public StartJobEvent(JstreamConf jstreamConf,String jobId) {
        super(jobId, JobEventType.JOB_RUN);
        this.jstreamConf = jstreamConf;
    }

    public JstreamConf getJstreamConf() {
        return jstreamConf;
    }

    public void setJstreamConf(JstreamConf jstreamConf) {
        this.jstreamConf = jstreamConf;
    }
}
