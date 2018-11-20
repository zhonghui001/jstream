package com.jyb.main;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.jyb.core.JobManager;
import com.jyb.jstream.config.JstreamConf;
import com.jyb.module.ServerModule;
import com.jyb.rpc.JstreamRpcServer;
import io.airlift.bootstrap.Bootstrap;
import io.airlift.json.JsonModule;
import io.airlift.log.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.service.CompositeService;

import javax.inject.Inject;
import static java.util.Objects.*;


/**
 * 主要入口
 */
public class JstreamServer extends CompositeService {

    private static final Logger log = Logger.get(JstreamServer.class);

    private JobManager jobManager;

    private JstreamRpcServer rpcServer;

    private JstreamConf jstreamConf;
    /**
     * Construct the service.
     *
     */
    @Inject
    public JstreamServer(JobManager jobManager,JstreamConf jstreamConf,
                         JstreamRpcServer rpcServer) {
        super("JstreamServer");

        this.jobManager = requireNonNull(jobManager,"jobmanager guice注入失败");
        this.jstreamConf = requireNonNull(jstreamConf,"jstream.properties配置文件注入失败");
        this.rpcServer = requireNonNull(rpcServer,"rpcserver guice注入失败");
    }

    @Override
    protected void serviceInit(Configuration conf) throws Exception {
        //添加状态机服务
        //添加rpc服务
        addIfService(rpcServer);
        super.serviceInit(conf);
    }

    @Override
    protected void serviceStart() throws Exception {
        super.serviceStart();
    }

    @Override
    protected void serviceStop() throws Exception {
        super.serviceStop();
    }

    public static void main(String[] args) {
        //System.setProperty("config","src/main/conf/jsteam.properties");
        ImmutableList.Builder<Module> modules = ImmutableList.builder();
        modules.add(new ServerModule(),
                new JsonModule()
                );
        Bootstrap app = new Bootstrap(modules.build());

        try{
            Injector injector = app.initialize();

            JstreamServer instance = injector.getInstance(JstreamServer.class);
            Configuration hadoopConf = injector.getInstance(Configuration.class);
            instance.init(hadoopConf);
            instance.start();

            System.out.println("rpc端口： "+instance.jstreamConf.getPort());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



}
