package com.jyb.sink;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.DataStreamWriter;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

public abstract class AbstractSink {

    protected void writeToSinkBase(DataStreamWriter<Row> writer,String processTime,String continuosTime,String checkPoint){
        if (StringUtils.isNotEmpty(processTime)){
            writer.trigger(Trigger.ProcessingTime(processTime));
        }else if(StringUtils.isNotEmpty(continuosTime)){
            writer.trigger(Trigger.Continuous(continuosTime));
        }
        StreamingQuery streamingQuery =writer.option("checkpointLocation", checkPoint)
                .start();
        try {
            streamingQuery.awaitTermination();
        } catch (StreamingQueryException e) {
            e.printStackTrace();
        }

    }
}
