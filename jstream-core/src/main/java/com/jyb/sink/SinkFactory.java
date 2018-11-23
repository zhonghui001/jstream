package com.jyb.sink;

import com.jyb.config.Config;
import com.jyb.config.JstreamContext;

public class SinkFactory {

    public static JstreamSink getSink(JstreamContext context){
        Config sinkConfig = context.getConfiguration().getSinkConfig();
        if (sinkConfig instanceof ConsoleSink.ConsoleSinkConfig)
            return new ConsoleSink();
        else if (sinkConfig instanceof MysqlSink.MysqlSinkConfig)
            return new MysqlSink();
        else if (sinkConfig instanceof RedisSink.RedisSinkConfig)
            return new RedisSink();
        else if (sinkConfig instanceof KafkaSink.KafkaSinkConfig)
            return new KafkaSink();
        return null;
    }
}
