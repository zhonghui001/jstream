package com.jyb.sink;

import com.jyb.config.Config;
import com.jyb.config.JstreamContext;

public class SinkFactory {

    public static JstreamSink getSink(JstreamContext context){
        Config sinkConfig = context.getConfiguration().getSinkConfig();
        if (sinkConfig instanceof ConsoleSink.ConsoleSinkConfig)
            return new ConsoleSink();
        if (sinkConfig instanceof MysqlSink.MysqlSinkConfig)
            return new MysqlSink();
        return null;
    }
}
