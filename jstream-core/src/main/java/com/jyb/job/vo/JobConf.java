package com.jyb.job.vo;

import com.jyb.config.ResouceConfig;

import static java.util.Objects.requireNonNull;

public class JobConf {


    String jarPath;

    String jobId;

    ResouceConfig resourceConfig;

    public JobConf(String jarPath, String jobId, ResouceConfig resourceConfig) {
        this.jarPath = jarPath;
        this.jobId = jobId;
        this.resourceConfig = resourceConfig;
    }

    public ResouceConfig getResourceConfig() {
        return resourceConfig;
    }

    public void setResourceConfig(ResouceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
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
                + jstreamBasePath + "mysql-connector-java-5.1.45.jar,"
                +jstreamBasePath+"commons-dbutils-1.7.jar";
        requireNonNull(resourceConfig);
        requireNonNull(resourceConfig.getMaster());
        requireNonNull(resourceConfig.getDriverMemory());
        requireNonNull(resourceConfig.getExecutorCores());
        requireNonNull(resourceConfig.getExecutorMemory());
        requireNonNull(resourceConfig.getNumExecutors());
        String ck = System.getenv("SPARK_CHECKPOINT_HOME");
        requireNonNull(ck);
        return requireNonNull(System.getenv("SPARK_HOME"), "SPARK_HOME未设置") + "/bin/spark-submit " +
                " --master "+resourceConfig.getMaster()+
                " --name "+jobId+
                " --driver-memory "+resourceConfig.getDriverMemory()+
                " --executor-memory "+resourceConfig.getExecutorMemory()+
                " --executor-cores "+resourceConfig.getExecutorCores()+
                " --num-executors "+ resourceConfig.getNumExecutors()+
                " --jars " + jarPath +
                " --conf 'spark.executor.extraJavaOptions=-XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/jstream.dump.bin' "+
                //" --files " + jstreamHome + "/conf/jstream.properties " +
                " --class " + "com.jyb.core.JStreamMain " +
                //" --conf JSTREAM_HOME="+jstreamHome+" "+
                jstreamHome + "/lib/jstream-spark-1.0-SNAPSHOT.jar" +
                " "+jobId+
                " '"+ck+"/jstream.properties'";


    }


}
