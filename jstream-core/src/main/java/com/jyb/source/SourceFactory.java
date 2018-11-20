package com.jyb.source;

import com.jyb.config.JstreamContext;

public class SourceFactory {

    public static JstreamSource getSource(JstreamContext context){
        //todo 未实现source工厂
        return new KafkaSource();
    }

}
