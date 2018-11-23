package com.jyb.config;

import org.apache.hadoop.io.Writable;
import org.jets3t.service.security.OAuth2Tokens;

import javax.ws.rs.FormParam;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 系统资源类
 */
public class ResouceConfig implements Config, Writable {

    String master="yarn-cluster";
    String driverMemory="1G";
    String executorMemory="2G";
    String executorCores="2";
    String numExecutors="2";
    private static final long serialVersionUID = -4080961482719235578L;


    public ResouceConfig() {
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(master);
        out.writeUTF(driverMemory);
        out.writeUTF(executorMemory);
        out.writeUTF(executorCores);
        out.writeUTF(numExecutors);

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        master = in.readUTF();
        driverMemory = in.readUTF();
        executorMemory = in.readUTF();
        executorCores = in.readUTF();
        numExecutors = in.readUTF();
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(String executorCores) {
        this.executorCores = executorCores;
    }

    public String getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(String numExecutors) {
        this.numExecutors = numExecutors;
    }
}
