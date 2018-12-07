package jyb.test;

import com.jyb.config.*;
import com.jyb.sink.AbstractSinkConfig;
import com.jyb.sink.ConsoleSink;
import com.jyb.source.KafkaSource;

public class MockConfiguration {

    public static JstreamConfiguration newInstance(){


        ResouceConfig resouceConfig = new ResouceConfig();
        ExtConfig extConfig = new ExtConfig("myapp");

        Config source = new KafkaSource.KafkaSouceConfig("10.23.181.177:9092,0.23.107.174:9092",
                "dbjyb",
                "","latest",null);

        AbstractSinkConfig sink = new ConsoleSink.ConsoleSinkConfig(new OutPutModeConfig("complete"),
                new TriggerConfig(),null,null);

        JstreamConfiguration conf = new JstreamConfiguration(resouceConfig,extConfig,source,sink);

        conf.addSqlEntry(new SqlEntry("select get_json_object(value,'$.dbTable') dbtable,get_json_object(value,'$.update_time') update_time  from topic","topic2",null))
                .addSqlEntry(new SqlEntry("select count(1) from topic2","",null));


        return conf;
    }
}
