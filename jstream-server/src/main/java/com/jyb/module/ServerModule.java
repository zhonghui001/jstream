package com.jyb.module;

import com.google.inject.*;
import com.jyb.core.StatementParser;
import com.jyb.core.StatementParserImpl;
import com.jyb.db.JobStreamService;
import com.jyb.job.Job;
import com.jyb.job.JobImpl;
import com.jyb.jstream.config.JstreamConf;
import com.jyb.main.JstreamServer;
import com.jyb.parser.antlr.AntlrSqlParser;
import com.jyb.rpc.JstreamRpcServer;
import com.jyb.yarn.SparkAppManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.client.api.TimelineClient;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.Dispatcher;

import static io.airlift.configuration.ConfigBinder.configBinder;

public class ServerModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(JstreamServer.class).in(Scopes.SINGLETON);
        binder.bind(Configuration.class).toInstance(new Configuration());
        binder.bind(JstreamRpcServer.class).in(Scopes.SINGLETON);
        //配置文件 注入
        configBinder(binder).bindConfig(JstreamConf.class);
        binder.bind(Dispatcher.class).to(AsyncDispatcher.class).in(Scopes.SINGLETON);
        binder.bind(Job.class).to(JobImpl.class).in(Scopes.SINGLETON);

        binder.bind(YarnClient.class).toProvider(YarnClientProvider.class).in(Scopes.SINGLETON);
        binder.bind(SparkAppManager.class).in(Scopes.SINGLETON);
        binder.bind(JobStreamService.class).in(Scopes.SINGLETON);
        binder.bind(AntlrSqlParser.class).in(Scopes.SINGLETON);
        binder.bind(StatementParser.class).to(StatementParserImpl.class).in(Scopes.SINGLETON);




    }


    public static class YarnClientProvider
            implements Provider<YarnClient>
    {


        @Override
        public YarnClient get()
        {
            YarnConfiguration yarnConfiguration = new YarnConfiguration();
            String yarnPath = System.getenv("HADOOP_HOME")+"/etc/hadoop/yarn-site.xml";
            String corePath = System.getenv("HADOOP_HOME")+"/etc/hadoop/core-site.xml";
            String hdfsPath = System.getenv("HADOOP_HOME")+"/etc/hadoop/hdfs-site.xml";
            yarnConfiguration.addResource(new Path(yarnPath));
            yarnConfiguration.addResource(new Path(corePath));
            yarnConfiguration.addResource(new Path(hdfsPath));
            YarnClient client = YarnClient.createYarnClient();
            try {
                TimelineClient.createTimelineClient();
            }
            catch (NoClassDefFoundError e) {
                //logger.warn("createTimelineClient() error with {}", TimelineClient.class.getResource(TimelineClient.class.getSimpleName() + ".class"), e);
                yarnConfiguration.setBoolean(YarnConfiguration.TIMELINE_SERVICE_ENABLED, false);
            }
            client.init(yarnConfiguration);
            client.start();
            return client;
        }
    }


}
