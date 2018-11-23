package com.jyb.vo;

import javax.ws.rs.FormParam;


public class JobModel {
    @FormParam("master")
    String master;

    @FormParam("driverMemory")
    String driverMemory;
    @FormParam("executorMemory")
    String executorMemory;
    @FormParam("executorCores")
    String executorCores;
    @FormParam("numExecutors")
    String numExecutors;

    @FormParam("id")
    String id;

    @FormParam("appName")
    String appName;

    @FormParam("sourceType")
    String sourceType;
    @FormParam("server")
    String server;
    @FormParam("groupId")
    String groupId;
    @FormParam("offsetMode")
    String offsetMode;
    @FormParam("topic")
    String topic;

    @FormParam("sql")
    String sql;

    @FormParam("outPutMode")
    String outPutMode;
    @FormParam("processInterval")
    String processInterval;

    @FormParam("continuosInterval")
    String continuosInterval;

    @FormParam("sinkType")
    String sinkType;

    //console sink
    @FormParam("numRows")
    String numRows;
    @FormParam("truncate")
    String truncate;

    //mysql sink

    @FormParam("mysqlSinkUrl")
    String mysqlSinkUrl;
    @FormParam("mysqlSinkUserName")
    String mysqlSinkUserName;
    @FormParam("mysqlSinkPassword")
    String mysqlSinkPassword;
    @FormParam("mysqlSinkDbName")
    String mysqlSinkDbName;
    @FormParam("mysqlSinkTable")
    String mysqlSinkTable;

    //kafka sink
    @FormParam("kafkaSinkServer")
    String kafkaSinkServer;
    @FormParam("kafkaSinkTopic")
    String kafkaSinkTopic;

    //redis sink
    @FormParam("redisHost")
    String redisHost;
    @FormParam("redisDbNo")
    String redisDbNo;
    @FormParam("redisPort")
    String redisPort;
    @FormParam("redisKey")
    String redisKey;


    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOffsetMode() {
        return offsetMode;
    }

    public void setOffsetMode(String offsetMode) {
        this.offsetMode = offsetMode;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getOutPutMode() {
        return outPutMode;
    }

    public void setOutPutMode(String outPutMode) {
        this.outPutMode = outPutMode;
    }



    public String getSinkType() {
        return sinkType;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }

    public String getNumRows() {
        return numRows;
    }

    public void setNumRows(String numRows) {
        this.numRows = numRows;
    }

    public String getTruncate() {
        return truncate;
    }

    public void setTruncate(String truncate) {
        this.truncate = truncate;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getProcessInterval() {
        return processInterval;
    }

    public void setProcessInterval(String processInterval) {
        this.processInterval = processInterval;
    }

    public String getContinuosInterval() {
        return continuosInterval;
    }

    public void setContinuosInterval(String continuosInterval) {
        this.continuosInterval = continuosInterval;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMysqlSinkUrl() {
        return mysqlSinkUrl;
    }

    public void setMysqlSinkUrl(String mysqlSinkUrl) {
        this.mysqlSinkUrl = mysqlSinkUrl;
    }

    public String getMysqlSinkUserName() {
        return mysqlSinkUserName;
    }

    public void setMysqlSinkUserName(String mysqlSinkUserName) {
        this.mysqlSinkUserName = mysqlSinkUserName;
    }

    public String getMysqlSinkPassword() {
        return mysqlSinkPassword;
    }

    public void setMysqlSinkPassword(String mysqlSinkPassword) {
        this.mysqlSinkPassword = mysqlSinkPassword;
    }

    public String getMysqlSinkDbName() {
        return mysqlSinkDbName;
    }

    public void setMysqlSinkDbName(String mysqlSinkDbName) {
        this.mysqlSinkDbName = mysqlSinkDbName;
    }

    public String getMysqlSinkTable() {
        return mysqlSinkTable;
    }

    public void setMysqlSinkTable(String mysqlSinkTable) {
        this.mysqlSinkTable = mysqlSinkTable;
    }

    public String getKafkaSinkServer() {
        return kafkaSinkServer;
    }

    public void setKafkaSinkServer(String kafkaSinkServer) {
        this.kafkaSinkServer = kafkaSinkServer;
    }

    public String getKafkaSinkTopic() {
        return kafkaSinkTopic;
    }

    public void setKafkaSinkTopic(String kafkaSinkTopic) {
        this.kafkaSinkTopic = kafkaSinkTopic;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public String getRedisDbNo() {
        return redisDbNo;
    }

    public void setRedisDbNo(String redisDbNo) {
        this.redisDbNo = redisDbNo;
    }

    public String getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(String redisPort) {
        this.redisPort = redisPort;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(String executorCores) {
        this.executorCores = executorCores;
    }

    public String getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(String numExecutors) {
        this.numExecutors = numExecutors;
    }
}
