package com.jyb.source;

import com.jyb.config.JstreamContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public interface JstreamSource {

    public Dataset<Row> createStream(SparkSession spark, JstreamContext context);
}
