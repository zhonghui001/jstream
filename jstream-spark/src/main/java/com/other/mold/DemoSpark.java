package com.other.mold;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;

public class DemoSpark {

    public static void main(String[] args)throws Exception {
        SparkSession spark = SparkSession.builder().appName("sampleWindows").enableHiveSupport().getOrCreate();
        Dataset<Row> df = spark.readStream().format("kafka")
                .option("kafka.bootstrap.servers", "10.23.181.177:9092,0.23.107.174:9092")
                .option("subscribe", "dbjyb")
                .option("failOnDataLoss", "false")
                .load().selectExpr("CAST(value AS STRING)");

        df.createOrReplaceTempView("topic");


        spark.sql("select get_json_object(value,'$.dbTable') dbtable,get_json_object(value,'$.update_time') update_time  from topic").createOrReplaceTempView("topic2");


        Dataset<Row> df2 = spark.sql("select count(1) from topic2");
        StreamingQuery streamingQuery = df2.writeStream().outputMode("complete")
                .format("console")
//                .trigger(Trigger.ProcessingTime("60 seconds"))
                .start();
        streamingQuery.awaitTermination();

    }
}
