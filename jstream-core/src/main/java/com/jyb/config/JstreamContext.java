package com.jyb.config;

public class JstreamContext {

    private JstreamConfiguration configuration;

    public JstreamContext(JstreamConfiguration configuration) {
        this.configuration = configuration;
    }

    public JstreamConfiguration getConfiguration() {
        return configuration;
    }
}
