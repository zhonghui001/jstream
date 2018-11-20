package com.jyb.sink;

import com.jyb.config.Config;
import com.jyb.config.JstreamContext;
import com.jyb.config.OutPutModeConfig;
import com.jyb.config.TriggerConfig;
import com.jyb.sink.writer.JdbcForeachWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.DataStreamWriter;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static java.util.Objects.*;

public class MysqlSink implements JstreamSink {

    @Override
    public void writeToSink(Dataset<Row> df, JstreamContext context) {
        MysqlSinkConfig sinkConfig = (MysqlSinkConfig)context.getConfiguration().getSinkConfig();

        OutPutModeConfig outPutModeConfig = sinkConfig.getOutPutModeConfig();
        DataStreamWriter<Row> writer = df.writeStream().outputMode(outPutModeConfig.getMode())
                .foreach(new JdbcForeachWriter(sinkConfig.getFullUrl(),sinkConfig.getUserName(),sinkConfig.getPassword(),sinkConfig.getTable()));

        if (StringUtils.isNotEmpty(sinkConfig.getTriggerConfig().getProcessTime())){
            writer.trigger(Trigger.ProcessingTime(sinkConfig.getTriggerConfig().getProcessTime()));
        }else if(StringUtils.isNotEmpty(sinkConfig.getTriggerConfig().getContinuosTime())){
            writer.trigger(Trigger.Continuous(sinkConfig.getTriggerConfig().getContinuosTime()));
        }
        StreamingQuery streamingQuery =writer.start();
        try {
            streamingQuery.awaitTermination();
        } catch (StreamingQueryException e) {
            e.printStackTrace();
        }

    }


    public static class MysqlSinkConfig implements Config, Writable {
        String url;
        String userName;
        String password;
        String driver = "com.mysql.jdbc.Driver";

        String dbName;
        String table;

        private OutPutModeConfig outPutModeConfig;
        private TriggerConfig triggerConfig;


        public MysqlSinkConfig() {

        }

        public MysqlSinkConfig(String url, String userName, String password, String dbName, String table,
                               OutPutModeConfig outPutModeConfig, TriggerConfig triggerConfig) {
            this.url = requireNonNull(url);
            this.userName = requireNonNull(userName);
            this.password = requireNonNull(password);
            this.dbName = requireNonNull(dbName);
            this.table = requireNonNull(table);
            this.outPutModeConfig = requireNonNull(outPutModeConfig);
            this.triggerConfig = requireNonNull(triggerConfig);

        }



        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(url);
            out.writeUTF(userName);
            out.writeUTF(password);
            out.writeUTF(dbName);
            out.writeUTF(table);
            ObjectWritable.writeObject(out,outPutModeConfig,OutPutModeConfig.class,null);
            ObjectWritable.writeObject(out,triggerConfig,TriggerConfig.class,null);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            url = in.readUTF();
            userName = in.readUTF();
            password = in.readUTF();
            dbName = in.readUTF();
            table = in.readUTF();

            outPutModeConfig = (OutPutModeConfig)ObjectWritable.readObject(in,null);
            triggerConfig = (TriggerConfig) ObjectWritable.readObject(in,null);


        }

        public String getFullUrl(){
            return url+"/"+dbName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public OutPutModeConfig getOutPutModeConfig() {
            return outPutModeConfig;
        }

        public void setOutPutModeConfig(OutPutModeConfig outPutModeConfig) {
            this.outPutModeConfig = outPutModeConfig;
        }

        public TriggerConfig getTriggerConfig() {
            return triggerConfig;
        }

        public void setTriggerConfig(TriggerConfig triggerConfig) {
            this.triggerConfig = triggerConfig;
        }

        @Override
        public String toString() {
            return "MysqlSinkConfig{" +
                    "url='" + url + '\'' +
                    ", userName='" + userName + '\'' +
                    ", password='" + password + '\'' +
                    ", driver='" + driver + '\'' +
                    ", dbName='" + dbName + '\'' +
                    ", table='" + table + '\'' +
                    ", outPutModeConfig=" + outPutModeConfig +
                    ", triggerConfig=" + triggerConfig +
                    '}';
        }
    }
}
