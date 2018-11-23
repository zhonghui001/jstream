package com.jyb.job;

import com.jyb.config.ResouceConfig;
import com.jyb.jstream.config.JstreamConf;

public class StartJobEvent extends JobEvent {

    private JstreamConf jstreamConf;
    private ResouceConfig resouceConfig;

    public StartJobEvent(JstreamConf jstreamConf,String jobId,ResouceConfig resouceConfig) {
        super(jobId, JobEventType.JOB_RUN);
        this.jstreamConf = jstreamConf;
        this.resouceConfig = resouceConfig;
    }

    public ResouceConfig getResouceConfig() {
        return resouceConfig;
    }

    public void setResouceConfig(ResouceConfig resouceConfig) {
        this.resouceConfig = resouceConfig;
    }

    public JstreamConf getJstreamConf() {
        return jstreamConf;
    }

    public void setJstreamConf(JstreamConf jstreamConf) {
        this.jstreamConf = jstreamConf;
    }
}
