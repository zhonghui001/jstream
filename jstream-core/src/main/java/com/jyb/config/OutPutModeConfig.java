package com.jyb.config;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OutPutModeConfig implements Config {

    private String mode = "append";

    public OutPutModeConfig(){}

    public OutPutModeConfig(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(mode);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        mode = in.readUTF();
    }
}
