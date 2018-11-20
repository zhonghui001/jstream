package com.jyb.core;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * 用于测试用的，忽略
 */
public class SparkTest {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().appName("mytest").getOrCreate();
        Dataset<String> txt = spark.read().textFile("/test");
        System.out.println(txt.count());
    }
}
