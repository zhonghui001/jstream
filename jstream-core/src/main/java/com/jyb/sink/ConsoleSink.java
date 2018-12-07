package com.jyb.sink;

import com.jyb.config.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;
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

public class ConsoleSink extends AbstractSink implements JstreamSink {


    @Override
    public void writeToSink(String jobId,Dataset<Row> df, JstreamContext context) {

        ConsoleSinkConfig sinkConfig = (ConsoleSinkConfig)context.getConfiguration().getSinkConfig();

        requireNonNull(context.getConfiguration().getExtConfig().getSparkCheckPointPath(),"checkpoint 不能为null,请检查jstream-env.sh");
        OutPutModeConfig outPutModeConfig = sinkConfig.getOutPutModeConfig();
        DataStreamWriter<Row> writer = df.writeStream().outputMode(outPutModeConfig.getMode())
                .format("console")
                .option("numRows", sinkConfig.getNumRows())
                .option("truncate", sinkConfig.getTruncate());

        writeToSinkBase(writer, sinkConfig.getTriggerConfig().getProcessTime(), sinkConfig.getTriggerConfig().getContinuosTime(),
                context.getConfiguration().getExtConfig().getSparkCheckPointPath());
    }

    @Name("consoleSink")
    public static class ConsoleSinkConfig extends AbstractSinkConfig implements Config, Writable {

        @Name("sink.console.numRows")
        String numRows="100";

        @Name("sink.console.truncate")
        String truncate="false";

        public ConsoleSinkConfig() {
            super(null,null);
        }

        public ConsoleSinkConfig(OutPutModeConfig outPutModeConfig, TriggerConfig triggerConfig, String numRows, String truncate) {
            super(outPutModeConfig,triggerConfig);
            this.outPutModeConfig = requireNonNull(outPutModeConfig,"outputmodeconfig不能为null");
            this.triggerConfig = requireNonNull(triggerConfig,"triggerConfig不能为null");
            if (StringUtils.isNotBlank(numRows))
                this.numRows = numRows;
            if (StringUtils.isNotBlank(truncate))
                this.truncate = truncate;
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


        @Override
        public void write(DataOutput out) throws IOException {
            ObjectWritable.writeObject(out,outPutModeConfig,OutPutModeConfig.class,null);
            ObjectWritable.writeObject(out,triggerConfig,TriggerConfig.class,null);
            out.writeUTF(numRows);
            out.writeUTF(truncate);

        }

        @Override
        public void readFields(DataInput in) throws IOException {
            outPutModeConfig = (OutPutModeConfig)ObjectWritable.readObject(in,null);
            triggerConfig = (TriggerConfig) ObjectWritable.readObject(in,null);
            numRows = in.readUTF();
            truncate = in.readUTF();

        }
    }

}
