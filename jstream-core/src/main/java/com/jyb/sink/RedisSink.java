package com.jyb.sink;

import com.jyb.config.Config;
import com.jyb.config.JstreamContext;
import com.jyb.config.OutPutModeConfig;
import com.jyb.config.TriggerConfig;
import com.jyb.sink.writer.RedisForeachWritter;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.DataStreamWriter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class RedisSink extends AbstractSink implements JstreamSink {
    @Override
    public void writeToSink(String jobId,Dataset<Row> df, JstreamContext context) {
        RedisSinkConfig sinkConfig = (RedisSinkConfig) context.getConfiguration().getSinkConfig();
        requireNonNull(context.getConfiguration().getExtConfig().getSparkCheckPointPath(),"checkpoint 不能为null,请检查jstream-env.sh");

        DataStreamWriter<Row> writer = df.writeStream().outputMode(sinkConfig.getOutPutModeConfig().getMode())
                .foreach(new RedisForeachWritter(sinkConfig.getHost(), sinkConfig.getPort(), sinkConfig.getDbNo(), sinkConfig.getRedisKey()));
        writeToSinkBase(writer, sinkConfig.getTriggerConfig().getProcessTime(), sinkConfig.getTriggerConfig().getContinuosTime(),
                context.getConfiguration().getExtConfig().getSparkCheckPointPath());
    }


    public static class RedisSinkConfig implements Config, Writable {
        private static final long serialVersionUID = 3044422946500893519L;
        String host;
        Integer port = 6739;
        Integer dbNo;
        String redisKey;

        private OutPutModeConfig outPutModeConfig;
        private TriggerConfig triggerConfig;


        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(host);
            out.writeInt(port);
            out.writeInt(dbNo);
            out.writeUTF(redisKey);

            ObjectWritable.writeObject(out, outPutModeConfig, OutPutModeConfig.class, null);
            ObjectWritable.writeObject(out, triggerConfig, TriggerConfig.class, null);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            host = in.readUTF();
            port = in.readInt();
            dbNo = in.readInt();
            redisKey = in.readUTF();

            outPutModeConfig = (OutPutModeConfig) ObjectWritable.readObject(in, null);
            triggerConfig = (TriggerConfig) ObjectWritable.readObject(in, null);
        }

        public RedisSinkConfig(String host, Integer port, Integer dbNo, String redisKey, OutPutModeConfig outPutModeConfig, TriggerConfig triggerConfig) {
            this.host = requireNonNull(host);
            this.port = requireNonNull(port);
            this.dbNo = requireNonNull(dbNo);
            this.redisKey = requireNonNull(redisKey);
            this.outPutModeConfig = requireNonNull(outPutModeConfig);
            this.triggerConfig = requireNonNull(triggerConfig);
        }

        public RedisSinkConfig() {
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getDbNo() {
            return dbNo;
        }

        public void setDbNo(Integer dbNo) {
            this.dbNo = dbNo;
        }

        public String getRedisKey() {
            return redisKey;
        }

        public void setRedisKey(String redisKey) {
            this.redisKey = redisKey;
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


    }
}
