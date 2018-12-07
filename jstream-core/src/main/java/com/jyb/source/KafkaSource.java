package com.jyb.source;

import com.jyb.config.*;
import org.apache.hadoop.io.Writable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class KafkaSource implements JstreamSource, Writable {

    @Override
    public Dataset<Row> createStream(SparkSession spark, JstreamContext context) {
        JstreamConfiguration conf = context.getConfiguration();
        KafkaSouceConfig source = (KafkaSouceConfig)conf.getSourceConfig();

        Dataset<Row> df = spark.readStream().format("kafka")
                .option("kafka.bootstrap.servers", source.getServer())
                .option("subscribe", source.getTopic())
                .option("failOnDataLoss", "false")
                .option("group.id",source.getGroupId())
                .option("auto.offset.reset",source.getOffsetMode())
                .load().selectExpr("CAST(value AS STRING)");
        df.registerTempTable(source.getAlias());
                //.dropDuplicates();
        return df;
    }

    @Override
    public void write(DataOutput out) throws IOException {

    }

    @Override
    public void readFields(DataInput in) throws IOException {

    }


    @Name("kafkaSource")
    public static class KafkaSouceConfig implements Config,Writable{

        private static final long serialVersionUID = -618964413542989195L;

        @NotNull()
        @Name("source.kafka.server")
        private String server;

        @NotNull
        @Name("source.kafka.topic")
        private String topic;

        @Name("source.kafka.groupId")
        private String groupId="";

        @Name("source.kafka.offsetMode")
        private String offsetMode="latest";

        private String alias="topic";

        public KafkaSouceConfig(){}

        public KafkaSouceConfig(String server, String topic, String groupId, String offsetMode,String alias) {
            this.server = server;
            this.topic = topic==null?"":topic;
            this.groupId = groupId==null?"":groupId;
            this.offsetMode = offsetMode == null?"":offsetMode;
            this.alias = alias==null?"topic":alias;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
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

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(server);
            out.writeUTF(topic);
            out.writeUTF(groupId);
            out.writeUTF(offsetMode);
            out.writeUTF(alias==null?"topic":alias);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            this.server = in.readUTF();
            this.topic = in.readUTF();
            this.groupId = in.readUTF();
            this.offsetMode = in.readUTF();
            this.alias = in.readUTF();

        }
    }

}
