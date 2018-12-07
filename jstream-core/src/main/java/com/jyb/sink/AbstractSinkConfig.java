package com.jyb.sink;

import com.jyb.config.*;

import static java.util.Objects.*;

public abstract class AbstractSinkConfig implements Config {
    protected OutPutModeConfig outPutModeConfig;
    protected TriggerConfig triggerConfig;

    public AbstractSinkConfig(OutPutModeConfig outPutModeConfig, TriggerConfig triggerConfig) {
        this.outPutModeConfig = outPutModeConfig;
        this.triggerConfig = triggerConfig;
    }

    public OutPutModeConfig getOutPutModeConfig() {
        return outPutModeConfig;
    }

    public void setOutPutModeConfig(OutPutModeConfig outPutModeConfig) {
        this.outPutModeConfig = outPutModeConfig;
    }

    public TriggerConfig getTriggerConfig() {
        return triggerConfig;
    }

    public void setTriggerConfig(TriggerConfig triggerConfig) {
        this.triggerConfig = triggerConfig;
    }
}
