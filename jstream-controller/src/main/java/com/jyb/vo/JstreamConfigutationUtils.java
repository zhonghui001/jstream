package com.jyb.vo;

import com.jyb.config.*;
import com.jyb.sink.ConsoleSink;
import com.jyb.sink.KafkaSink;
import com.jyb.sink.MysqlSink;
import com.jyb.sink.RedisSink;
import com.jyb.source.KafkaSource;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class JstreamConfigutationUtils {

    public static JobModel parse(JstreamConfiguration conf){
        JobModel jobModel = new JobModel();
        //extconfig
        jobModel.setAppName(conf.getExtConfig().getAppName());
        //resouceconfig
        ResouceConfig resouceConfig = conf.getResouceConfig();
        jobModel.setMaster(resouceConfig.getMaster());
        jobModel.setDriverMemory(resouceConfig.getDriverMemory());
        jobModel.setExecutorCores(resouceConfig.getExecutorCores());
        jobModel.setExecutorMemory(resouceConfig.getExecutorMemory());
        jobModel.setNumExecutors(resouceConfig.getNumExecutors());

        //source
        if (conf.getSourceConfig() instanceof KafkaSource.KafkaSouceConfig){
            KafkaSource.KafkaSouceConfig souceConfig = (KafkaSource.KafkaSouceConfig)conf.getSourceConfig();
            jobModel.setSourceType("kafka");
            jobModel.setServer(souceConfig.getServer());
            jobModel.setGroupId(souceConfig.getGroupId());
            jobModel.setOffsetMode(souceConfig.getOffsetMode());
            jobModel.setTopic(souceConfig.getTopic());
        }

        //sql
        String sql = convertEntryToSql(conf.getSqlEntryList());
        jobModel.setSql(sql);

        //sink
        if (conf.getSinkConfig() instanceof ConsoleSink.ConsoleSinkConfig){
            ConsoleSink.ConsoleSinkConfig sinkConfig = (ConsoleSink.ConsoleSinkConfig)conf.getSinkConfig();
            jobModel.setOutPutMode(sinkConfig.getOutPutModeConfig().getMode());
            jobModel.setProcessInterval(sinkConfig.getTriggerConfig().getProcessTime());
            jobModel.setSinkType("console");
            jobModel.setNumRows(sinkConfig.getNumRows());
            jobModel.setTruncate(sinkConfig.getTruncate());
        }else if (conf.getSinkConfig() instanceof MysqlSink.MysqlSinkConfig){
            MysqlSink.MysqlSinkConfig sinkConfig = (MysqlSink.MysqlSinkConfig)conf.getSinkConfig();
            jobModel.setOutPutMode(sinkConfig.getOutPutModeConfig().getMode());
            jobModel.setProcessInterval(sinkConfig.getTriggerConfig().getProcessTime());
            jobModel.setSinkType("mysql");

            jobModel.setMysqlSinkUrl(sinkConfig.getUrl());
            jobModel.setMysqlSinkUserName(sinkConfig.getUserName());
            jobModel.setMysqlSinkPassword(sinkConfig.getPassword());
            jobModel.setMysqlSinkTable(sinkConfig.getTable());
            jobModel.setMysqlSinkDbName(sinkConfig.getDbName());

        }else if (conf.getSinkConfig() instanceof RedisSink.RedisSinkConfig){
            RedisSink.RedisSinkConfig sinkConfig = (RedisSink.RedisSinkConfig) conf.getSinkConfig();
            jobModel.setOutPutMode(sinkConfig.getOutPutModeConfig().getMode());
            jobModel.setProcessInterval(sinkConfig.getTriggerConfig().getProcessTime());
            jobModel.setSinkType("redis");

            jobModel.setRedisDbNo(String.valueOf(sinkConfig.getDbNo()));
            jobModel.setRedisHost(String.valueOf(sinkConfig.getHost()));
            jobModel.setRedisPort(String.valueOf(sinkConfig.getPort()));
            jobModel.setRedisKey(String.valueOf(sinkConfig.getRedisKey()));



        }else if(conf.getSinkConfig() instanceof KafkaSink.KafkaSinkConfig){
            KafkaSink.KafkaSinkConfig sinkConfig = (KafkaSink.KafkaSinkConfig) conf.getSinkConfig();
            jobModel.setOutPutMode(sinkConfig.getOutPutModeConfig().getMode());
            jobModel.setProcessInterval(sinkConfig.getTriggerConfig().getProcessTime());

            jobModel.setKafkaSinkServer(sinkConfig.getServer());
            jobModel.setKafkaSinkTopic(sinkConfig.getTopic());
            jobModel.setSinkType("kafka");
        }else{
            throw new RuntimeException("不支持类型");
        }

        return jobModel;

    }

    public static JstreamConfiguration convert(JobModel jobModel) {
        JstreamConfiguration configuration = new JstreamConfiguration();

        ResouceConfig resouceConfig = new ResouceConfig();
        resouceConfig.setDriverMemory(jobModel.getDriverMemory());
        resouceConfig.setExecutorCores(jobModel.getExecutorCores());
        resouceConfig.setExecutorMemory(jobModel.getExecutorMemory());
        resouceConfig.setMaster(jobModel.getMaster());
        resouceConfig.setNumExecutors(jobModel.getNumExecutors());
        configuration.setResouceConfig(resouceConfig);

        ExtConfig extConfig = new ExtConfig(jobModel.getAppName());
        configuration.setExtConfig(extConfig);

        //source
        if (StringUtils.equals(jobModel.getSourceType(),"kafka")){
            KafkaSource.KafkaSouceConfig souceConfig = new KafkaSource.KafkaSouceConfig();
            souceConfig.setServer(jobModel.getServer());
            souceConfig.setGroupId(jobModel.getGroupId());
            souceConfig.setOffsetMode(jobModel.getOffsetMode());
            souceConfig.setTopic(jobModel.getTopic());
            configuration.setSourceConfig(souceConfig);
        }else{
            throw new RuntimeException("不支持其他类型的source，source type为："+jobModel.getSourceType());
        }

        //process sql
        String sqls = jobModel.getSql().trim();
        if (StringUtils.isBlank(sqls))
            throw new RuntimeException("sql为必填项");
        String[] sqlArray = sqls.split(";");
        for (String sql:sqlArray){
            SqlEntry sqlEntry = parseSqlToEntry(sql);
            configuration.addSqlEntry(sqlEntry);
        }

        //sink
        if (StringUtils.equals(jobModel.getSinkType(),"console")){
            ConsoleSink.ConsoleSinkConfig sinkConfig = new ConsoleSink.ConsoleSinkConfig();
            sinkConfig.setNumRows(StringUtils.isEmpty(jobModel.getNumRows())?"10":jobModel.getNumRows());
            sinkConfig.setTruncate(StringUtils.isEmpty(jobModel.getTruncate())?"true":jobModel.getTruncate());

            TriggerConfig triggerConfig = new TriggerConfig(jobModel.getProcessInterval(),jobModel.getContinuosInterval());
            sinkConfig.setTriggerConfig(triggerConfig);
            sinkConfig.setOutPutModeConfig(new OutPutModeConfig(jobModel.getOutPutMode()));
            configuration.setSinkConfig(sinkConfig);
        }else if(StringUtils.equals(jobModel.getSinkType(),"mysql")){
            MysqlSink.MysqlSinkConfig sinkConfig = new MysqlSink.MysqlSinkConfig();
            sinkConfig.setUrl(jobModel.getMysqlSinkUrl());
            sinkConfig.setDbName(jobModel.getMysqlSinkDbName());
            sinkConfig.setUserName(jobModel.getMysqlSinkUserName());
            sinkConfig.setPassword(jobModel.getMysqlSinkPassword());
            sinkConfig.setTable(jobModel.getMysqlSinkTable());

            TriggerConfig triggerConfig = new TriggerConfig(jobModel.getProcessInterval(),jobModel.getContinuosInterval());
            sinkConfig.setTriggerConfig(triggerConfig);
            sinkConfig.setOutPutModeConfig(new OutPutModeConfig(jobModel.getOutPutMode()));
            configuration.setSinkConfig(sinkConfig);
        }else if(StringUtils.equals(jobModel.getSinkType(),"redis")){
            RedisSink.RedisSinkConfig sinkConfig = new RedisSink.RedisSinkConfig();
            sinkConfig.setDbNo(Integer.parseInt(jobModel.getRedisDbNo()));
            sinkConfig.setHost(jobModel.getRedisHost());
            sinkConfig.setPort(Integer.parseInt(jobModel.getRedisPort()));
            sinkConfig.setRedisKey(jobModel.getRedisKey());

            TriggerConfig triggerConfig = new TriggerConfig(jobModel.getProcessInterval(),jobModel.getContinuosInterval());
            sinkConfig.setTriggerConfig(triggerConfig);
            sinkConfig.setOutPutModeConfig(new OutPutModeConfig(jobModel.getOutPutMode()));
            configuration.setSinkConfig(sinkConfig);

        }else if(StringUtils.equals(jobModel.getSinkType(),"kafka")){
            KafkaSink.KafkaSinkConfig sinkConfig = new KafkaSink.KafkaSinkConfig();
            sinkConfig.setServer(jobModel.getKafkaSinkServer());
            sinkConfig.setTopic(jobModel.getKafkaSinkTopic());

            TriggerConfig triggerConfig = new TriggerConfig(jobModel.getProcessInterval(),jobModel.getContinuosInterval());
            sinkConfig.setTriggerConfig(triggerConfig);
            sinkConfig.setOutPutModeConfig(new OutPutModeConfig(jobModel.getOutPutMode()));
            configuration.setSinkConfig(sinkConfig);
        } else{
            throw new RuntimeException("不支持sink类型");
        }


        return configuration;
    }

    private static String convertEntryToSql(List<SqlEntry> sqlEntryList){
        StringBuilder sb = new StringBuilder();
        if (sqlEntryList.size()==1)
            sb.append(sqlEntryList.get(0).getSql());
        else{
            for (SqlEntry sqlEntry:sqlEntryList){
                if (StringUtils.isNotBlank(sqlEntry.getAlias())){
                    sb.append("with ( ");
                    sb.append(sqlEntry.getSql());
                    sb.append(" ) as ");
                    sb.append(sqlEntry.getAlias());
                    sb.append(";");
                }else{
                    sb.append(sqlEntry.getSql());
                }

            }
        }
        return sb.toString();
    }

    private static SqlEntry parseSqlToEntry(String sql){
        try {
            if (StringUtils.contains(sql,"with") && StringUtils.contains(sql,"as")){
                String one = StringUtils.replaceFirst(sql, "with", "");
                String two = StringUtils.replaceFirst(one, "\\(", "");
                int pos = StringUtils.lastIndexOf(two, ")");
                String clause=StringUtils.substring(two,0,pos).trim();
                String aliase = StringUtils.substring(two,pos+1,two.length()).trim().replaceAll("as ","").trim();
                return new SqlEntry(clause,aliase);
            }else{
                return new SqlEntry(sql,"");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException("请检查sql语句是否符合要求");
        }
    }


}
