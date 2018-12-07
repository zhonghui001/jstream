package com.jyb.sink;

import com.jyb.config.*;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.DataStreamWriter;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

import static java.util.Objects.*;
import static org.apache.spark.sql.functions.*;
public class KafkaSink extends AbstractSink implements JstreamSink {
    @Override
    public void writeToSink(String jobId,Dataset<Row> df, JstreamContext context) {
        KafkaSinkConfig sinkConfig = (KafkaSinkConfig)context.getConfiguration().getSinkConfig();
        requireNonNull(sinkConfig.getServer(),"kafka server不能为null");
        requireNonNull(sinkConfig.getTopic(),"kafka topic不能为null");
        requireNonNull(context.getConfiguration().getExtConfig().getSparkCheckPointPath(),"checkpoint 不能为null,请检查jstream-env.sh");


        Column[] columns = getColumns(df);
        Dataset<Row> df2 = df.select(to_json(struct(columns)).alias("value"));
        DataStreamWriter<Row> writer = df2.writeStream().format("kafka")
                .option("kafka.bootstrap.servers", sinkConfig.getServer())
                .option("topic", sinkConfig.getTopic())
                .outputMode(sinkConfig.getOutPutModeConfig().getMode());

        writeToSinkBase(writer,sinkConfig.getTriggerConfig().getProcessTime(),
                sinkConfig.getTriggerConfig().getContinuosTime(),context.getConfiguration().getExtConfig().getSparkCheckPointPath());
    }

    private Column[] getColumns(Dataset<Row> df){

        ArrayList<Column> list = new ArrayList<>();
        String[] columns = df.columns();
        for (String column:columns){
            list.add(df.col(column));
        }
        return list.toArray(new Column[]{});
    }



    @Name("kafkaSink")
    public static class KafkaSinkConfig extends AbstractSinkConfig implements Writable{

        private static final long serialVersionUID = 4012041513996711004L;
        @NotNull
        @Name("sink.kafka.server")
        private String server;

        @NotNull
        @Name("sink.kafka.topic")
        private String topic;

        public KafkaSinkConfig() {
            super(null,null);
        }

        public KafkaSinkConfig(String server, String topic, OutPutModeConfig outPutModeConfig, TriggerConfig triggerConfig) {
            super(outPutModeConfig,triggerConfig);
            this.server = requireNonNull(server);
            this.topic = requireNonNull(topic);

        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(server);
            out.writeUTF(topic);
            ObjectWritable.writeObject(out,outPutModeConfig,OutPutModeConfig.class,null);
            ObjectWritable.writeObject(out,triggerConfig,TriggerConfig.class,null);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            server = in.readUTF();
            topic = in.readUTF();
            outPutModeConfig =(OutPutModeConfig)ObjectWritable.readObject(in,null);
            triggerConfig = (TriggerConfig)ObjectWritable.readObject(in,null);
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


    }
}
