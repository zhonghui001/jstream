package com.jyb.core;

import com.jyb.job.Job;

public class JobRunner extends Thread {

    Job job;

    public JobRunner(Job Job){
        this.job = job;
    }

    @Override
    public void run() {
        //job.saveJob();
    }
}
