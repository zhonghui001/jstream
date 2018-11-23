package com.jyb.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TriggerConfig implements Config, Writable {

    private static final long serialVersionUID = -6015514211413312319L;
    String processTime="";
    String continuosTime="";

    public TriggerConfig() {
    }

    public TriggerConfig(String processTime, String continuosTime) {
        this.processTime = StringUtils.isEmpty(processTime)?"":processTime;
        this.continuosTime = StringUtils.isEmpty(continuosTime)?"":continuosTime;
    }


    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(processTime);
        out.writeUTF(continuosTime);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        processTime = in.readUTF();
        continuosTime = in.readUTF();
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public String getContinuosTime() {
        return continuosTime;
    }

    public void setContinuosTime(String continuosTime) {
        this.continuosTime = continuosTime;
    }
}
