package com.jyb.config;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 补充配置 入appname
 */
public class ExtConfig implements Config, Writable {

    public String appName;

    public ExtConfig(){}

    public ExtConfig(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(appName);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.appName = in.readUTF();
    }
}
