package com.jyb.jstream.config;

import io.airlift.configuration.Config;
import static java.util.Objects.*;

/**
 * jstream配置文件
 */
public class JstreamConf {

    Integer port;


    String jdbcType;
    String jdbcUrl;
    String jdbcUsername;
    String jdbcPassword;

    String jstreamHome;
    String sparkHome;
    String hadoopHome;

    public String getJdbcType() {
        return jdbcType;
    }

    @Config("jdbc.type")
    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    @Config("jdbc.url")
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    @Config("jdbc.username")
    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    @Config("jdbc.password")
    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    @Config("server.rpc.port")
    public JstreamConf setPort(String port)
    {
        this.port = Integer.parseInt(port);
        return this;
    }

    public String getJstreamHome() {
        return requireNonNull(System.getenv("JSTREAM_HOME"),"JSTREAM_HOME 必须被指定");
    }



    public String getSparkHome() {
        return requireNonNull(System.getenv("SPARK_HOME"),"SPARK_HOME必须被指定");
    }



    public String getHadoopHome() {
        return requireNonNull(System.getenv("HADOOP_HOME"),"HADOOP_HOME必须被指定");
    }



    public Integer getPort(){
        return port;
    }



}
