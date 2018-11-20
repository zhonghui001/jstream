package com.jyb.yarn;

import com.google.inject.Inject;
import com.jyb.core.JStreamMain;
import com.jyb.core.SparkTest;
import com.jyb.job.vo.JobConf;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;

import java.io.IOException;
import java.util.Objects;


public class SparkAppManager {


    private YarnClient yarnClient;

    @Inject
    public SparkAppManager(YarnClient yarnClient){
        this.yarnClient = yarnClient;
    }



    public ApplicationId submitApp(JobConf jobConf)
            throws Exception {
        //spark要求必须要有spark_home环境变量
        assert StringUtils.isNotBlank(System.getenv("SPARK_HOME"));

        System.setProperty("SPARK_YARN_MODE", "true");
        SparkConf sparkConf = new SparkConf();

        sparkConf.setMaster("yarn");
        sparkConf.setAppName(jobConf.getJobId());

        sparkConf.set("spark.submit.deployMode", "yarn");
        //sparkConf.set("spark.yarn.jars","hdfs://bigdata/spark/spark_jars2/*");
        sparkConf.set("spark.yarn.jars","hdfs://zhonghui:9000/spark/jars/*");

        setDistJars(sparkConf,jobConf);


        ClientArguments clientArguments = new ClientArguments(getArgs(jobConf));   // spark-2.0.0
        Client appClient = new SparkYarnClient(clientArguments, sparkConf, yarnClient);


        return appClient.submitApplication();
    }



    private static void setDistJars(SparkConf sparkConf,JobConf jobConf)
            throws IOException {
        String path = jobConf.getJarPath()+","+Objects.requireNonNull(System.getenv("JSTREAM_HOME"),"JSTREAM_HOME 不能为null") +"/conf/jstream.properties";
        sparkConf.set("spark.yarn.dist.jars", path);   //上传配置文件
    }

    private String[] getArgs(JobConf jobConf)
    {
        return new String[] {
                //"--name",
                //"test-SparkPi",

                //"--driver-memory",
                //"1000M",

                "--jar", jobConf.getJarPath(),

                //"--class", JStreamMain.class.getName(),

                "--class", SparkTest.class.getName(),
                //"--files", Objects.requireNonNull(System.getenv("JSTREAM_HOME"),"JSTREAM_HOME 不能为null") +"/conf/jstream.properties",
                // argument 1 to my Spark program
                //"--arg", slices   用户自定义的参数

                // argument 2 to my Spark program (helper argument to create a proper JavaSparkContext object)
                //"--arg",
        };
    }


//    public static void main(String[] args) throws Exception {
//        SparkAppManager sparkAppLauncher = new SparkAppManager();
//        sparkAppLauncher.submitApp();
//    }
//        yarnClient = YarnClient.createYarnClient();
//        try {
//            TimelineClient.createTimelineClient();
//        }
//        catch (NoClassDefFoundError e) {
//            //logger.warn("createTimelineClient() error with {}", TimelineClient.class.getResource(TimelineClient.class.getSimpleName() + ".class"), e);
//            //yarnConfiguration.setBoolean(YarnConfiguration.TIMELINE_SERVICE_ENABLED, false);
//        }
//        YarnConfiguration conf = new YarnConfiguration();
//        conf.addResource(new Path("/data/hadoop/etc/hadoop/yarn-site.xml"));
//        conf.addResource(new Path("/data/hadoop/etc/hadoop/core-site.xml"));
//        conf.addResource(new Path("/data/hadoop/etc/hadoop/hdfs-site.xml"));
//        yarnClient.init(new YarnConfiguration());
//        yarnClient.start();


}
