package com.jyb.config;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import static java.util.Objects.*;

/**
 * 补充配置 入appname
 */
public class ExtConfig implements Config, Writable {

    private static final long serialVersionUID = 2674846108262364197L;
    public String appName;
    public String sparkCheckPointPath=requireNonNull(System.getenv("SPARK_CHECKPOINT_HOME"),"SPARK_CHECKPOINT_HOME未设置");

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

    public String getSparkCheckPointPath() {
        return sparkCheckPointPath;
    }

    public void setSparkCheckPointPath(String sparkCheckPointPath) {
        this.sparkCheckPointPath = sparkCheckPointPath;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(appName);
        out.writeUTF(sparkCheckPointPath);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.appName = in.readUTF();
        this.sparkCheckPointPath = in.readUTF();
    }
}
