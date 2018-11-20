package com.jyb.job;

import com.jyb.config.JstreamConfiguration;
import com.jyb.jstream.config.JstreamConf;

public class InitJobEvent extends JobEvent  {

    private JstreamConfiguration jstreamConfiguration;
    private JstreamConf conf;
    public InitJobEvent(String jobId, JstreamConfiguration jstreamConfiguration, JstreamConf conf) {
        super(jobId, JobEventType.JOB_INIT);
        this.jstreamConfiguration  = jstreamConfiguration;
        this.conf = conf;
    }

    public JstreamConf getConf() {
        return conf;
    }

    public void setConf(JstreamConf conf) {
        this.conf = conf;
    }

    public JstreamConfiguration getJstreamConfiguration() {
        return jstreamConfiguration;
    }

    public void setJstreamConfiguration(JstreamConfiguration jstreamConfiguration) {
        this.jstreamConfiguration = jstreamConfiguration;
    }
}
