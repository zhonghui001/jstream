package com.jyb.sink;

import com.jyb.config.JstreamContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface JstreamSink {


    public void writeToSink(String jobId,Dataset<Row> df, JstreamContext context);


}
