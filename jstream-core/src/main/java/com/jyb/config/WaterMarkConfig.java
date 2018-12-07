package com.jyb.config;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class WaterMarkConfig implements Serializable, Writable {

    private static final long serialVersionUID = -3476098873080904893L;

    private String filedName="";
    private String expression="";

    public WaterMarkConfig() {
    }

    public WaterMarkConfig(String filedName, String expression) {
        this.filedName = filedName;
        this.expression = expression;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "WaterMarkConfig{" +
                "filedName='" + filedName + '\'' +
                ", expression='" + expression + '\'' +
                '}';
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(filedName);
        out.writeUTF(expression);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        filedName = in.readUTF();
        expression = in.readUTF();

    }

    public boolean isEmpty(){
        if ("".equals(filedName))
            return true;
        else
            return false;
    }
}
