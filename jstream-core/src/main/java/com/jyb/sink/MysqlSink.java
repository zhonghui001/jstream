package com.jyb.sink;

import com.jyb.config.*;
import com.jyb.sink.writer.JdbcForeachWriter;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.DataStreamWriter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class MysqlSink extends AbstractSink implements JstreamSink {

    @Override
    public void writeToSink(String jobId,Dataset<Row> df, JstreamContext context) {
        MysqlSinkConfig sinkConfig = (MysqlSinkConfig)context.getConfiguration().getSinkConfig();

        requireNonNull(context.getConfiguration().getExtConfig().getSparkCheckPointPath(),"checkpoint 不能为null,请检查jstream-env.sh");

        OutPutModeConfig outPutModeConfig = sinkConfig.getOutPutModeConfig();
        DataStreamWriter<Row> writer = df.writeStream().outputMode(outPutModeConfig.getMode())
                .foreach(new JdbcForeachWriter(sinkConfig.getFullUrl(),sinkConfig.getUserName(),sinkConfig.getPassword(),sinkConfig.getTable()));

        super.writeToSinkBase(writer,sinkConfig.getTriggerConfig().getProcessTime(),
                sinkConfig.getTriggerConfig().getContinuosTime(),context.getConfiguration().getExtConfig().getSparkCheckPointPath());

    }


    @Name("mysqlSink")
    public static class MysqlSinkConfig extends AbstractSinkConfig implements Config, Writable {


        private static final long serialVersionUID = 5322425870670043910L;
        @NotNull
        @Name("sink.mysql.url")
        String url;

        @NotNull
        @Name("sink.mysql.userName")
        String userName;

        @NotNull
        @Name("sink.mysql.password")
        String password;

        @Name("sink.mysql.driver")
        String driver = "com.mysql.jdbc.Driver";

        @NotNull
        @Name("sink.mysql.dbName")
        String dbName;

        @NotNull
        @Name("sink.mysql.table")
        String table;


        public MysqlSinkConfig() {
            super(null,null);
        }

        public MysqlSinkConfig(String url, String userName, String password, String dbName, String table,
                               OutPutModeConfig outPutModeConfig, TriggerConfig triggerConfig) {
            super(outPutModeConfig,triggerConfig);
            this.url = requireNonNull(url);
            this.userName = requireNonNull(userName);
            this.password = requireNonNull(password);
            this.dbName = requireNonNull(dbName);
            this.table = requireNonNull(table);


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
