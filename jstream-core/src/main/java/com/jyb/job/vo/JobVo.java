package com.jyb.job.vo;

import com.jyb.config.JstreamConfiguration;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class JobVo implements Serializable, Writable {
    Integer id;
    String appName;
    String confPath;
    String jobState;
    String applicationId;
    String applicationState;
    JstreamConfiguration configuration;
    String sqlStr="";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfPath() {
        return confPath;
    }

    public void setConfPath(String confPath) {
        this.confPath = confPath;
    }

    public String getJobState() {
        return jobState;
    }

    public void setJobState(String jobState) {
        this.jobState = jobState;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(String applicationState) {
        this.applicationState = applicationState;
    }

    public JstreamConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(JstreamConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getSqlStr() {
        return sqlStr;
    }

    public void setSqlStr(String sqlStr) {
        this.sqlStr = sqlStr==null?"":sqlStr;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeUTF(appName);
        dataOutput.writeUTF(confPath);
        dataOutput.writeUTF(jobState);
        dataOutput.writeUTF(applicationId);
        dataOutput.writeUTF(applicationState);
        ObjectWritable.writeObject(dataOutput,configuration, configuration.getClass(),null);
        dataOutput.writeUTF(sqlStr);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        id = dataInput.readInt();
        appName = dataInput.readUTF();
        confPath = dataInput.readUTF();
        jobState = dataInput.readUTF();
        applicationId = dataInput.readUTF();
        applicationState = dataInput.readUTF();
        configuration = (JstreamConfiguration) ObjectWritable.readObject(dataInput,null);
        sqlStr = dataInput.readUTF();

    }
}
