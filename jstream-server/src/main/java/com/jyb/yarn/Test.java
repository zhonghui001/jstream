package com.jyb.yarn;

import com.jyb.job.vo.JobConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.client.api.TimelineClient;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

public class Test {

    public static void main(String[] args)throws Exception {


        Test test = new Test();
        YarnClient yarnClient = test.get();



        SparkAppManager sparkAppManager = new SparkAppManager(yarnClient);
        JobConf jobConf = new JobConf("/home/zh/Desktop/jstream-Distribution-1.0-SNAPSHOT/lib/jstream-core-1.0-SNAPSHOT.jar," +
                "/home/zh/Desktop/jstream-Distribution-1.0-SNAPSHOT/lib/jstream-spark-1.0-SNAPSHOT.jar","jobid"
                );

        sparkAppManager.submitApp(jobConf);
    }


    public YarnClient get()
    {
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        String yarnPath = System.getenv("HADOOP_HOME")+"/etc/hadoop/yarn-site.xml";
        String corePath = System.getenv("HADOOP_HOME")+"/etc/hadoop/core-site.xml";
        String hdfsPath = System.getenv("HADOOP_HOME")+"/etc/hadoop/hdfs-site.xml";
        yarnConfiguration.addResource(new Path(yarnPath));
        yarnConfiguration.addResource(new Path(corePath));
        yarnConfiguration.addResource(new Path(hdfsPath));
        System.out.println(yarnPath);
        YarnClient client = YarnClient.createYarnClient();
        try {
            TimelineClient.createTimelineClient();
        }
        catch (NoClassDefFoundError e) {
            //logger.warn("createTimelineClient() error with {}", TimelineClient.class.getResource(TimelineClient.class.getSimpleName() + ".class"), e);
            yarnConfiguration.setBoolean(YarnConfiguration.TIMELINE_SERVICE_ENABLED, false);
        }
        client.init(yarnConfiguration);
        client.start();
        return client;
    }
}
