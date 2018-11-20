package com.jyb.job.vo;

import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.*;

public class JobConf {


    String jarPath;

    String jobId;

    public JobConf(String jarPath, String jobId) {
        this.jarPath = jarPath;
        this.jobId = jobId;
    }


    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCommandLine() {
        String jstreamHome = requireNonNull(System.getenv("JSTREAM_HOME"), "JSTREAM_HOME未设置");
        String jstreamBasePath = jstreamHome + "/lib/";
        String jarPath = jstreamBasePath + "jstream-core-1.0-SNAPSHOT.jar,"
                + jstreamBasePath + "mysql-connector-java-5.1.45.jar"
                +jstreamBasePath+"commons-dbutils-1.7.jar";

        return requireNonNull(System.getenv("SPARK_HOME"), "SPARK_HOME未设置") + "/bin/spark-submit --master yarn-client " +
                " --jars " + jarPath +
                " --files " + jstreamHome + "/conf/jstream.properties " +
                " --class " + "com.jyb.core.JStreamMain " +
                //"-DJSTREAM_HOME="+jstreamHome+" "+
                jstreamHome + "/lib/jstream-spark-1.0-SNAPSHOT.jar" +
                " "+jobId;

    }


}
