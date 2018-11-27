package com.jyb.core;

import org.apache.spark.sql.streaming.StreamingQueryListener;

import java.util.Properties;

public class QueryLinstener extends StreamingQueryListener {

    Properties properties;
    String jobId;


    @Override
    public void onQueryStarted(QueryStartedEvent event) {
        System.out.println(event.id()+"查询启动中。。。。。。");

    }


    @Override
    public void onQueryProgress(QueryProgressEvent event) {
        System.out.println(event.progress()+"查询process。。。。。。");
    }

    @Override
    public void onQueryTerminated(QueryTerminatedEvent event) {
        System.out.println(event.id()+"查询结束中。。。。。。");
    }
}
