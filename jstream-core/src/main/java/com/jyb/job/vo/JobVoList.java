package com.jyb.job.vo;


import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobVoList implements Serializable, Writable {

    List<JobVo> jobVos = new ArrayList<JobVo>();

    public JobVoList(){}

    public JobVoList(List<JobVo> jobVos) {
        this.jobVos = jobVos;
    }

    public List<JobVo> getJobVos() {
        return jobVos;
    }

    public void setJobVos(List<JobVo> jobVos) {
        this.jobVos = jobVos;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        JobVo[] en = jobVos.toArray(new JobVo[]{});
        ObjectWritable.writeObject(out,en,en.getClass(),null);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        JobVo[] ens = (JobVo[]) ObjectWritable.readObject(in,null);
        Arrays.stream(ens).forEach(jobVos::add);
    }
}
