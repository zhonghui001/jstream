package com.jyb.rpc;

import com.jyb.job.Job;
import com.jyb.job.JobImpl;
import com.jyb.jstream.config.JstreamConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.service.CompositeService;



import javax.inject.Inject;
import java.net.InetAddress;
import static java.util.Objects.*;

public class JstreamRpcServer extends CompositeService {

    //hadoop config
    Configuration conf;

    JstreamConf jstreamConf;

    Job job;

    /**
     * Construct the service.
     *
     */
    @Inject
    public JstreamRpcServer(Configuration conf, JstreamConf jstreamConf, Job job) {
        super("JstreamRpcServer");
        this.conf = requireNonNull(conf,"hadoop configuration不能为null");
        this.jstreamConf = requireNonNull(jstreamConf,"配置文件jstream.properties注入失败");
        this.job = requireNonNull(job,"job 不能为null");

    }

    @Override
    protected void serviceInit(Configuration conf) throws Exception {
        this.conf = conf;
        addIfService(job);
        super.serviceInit(conf);

    }

    @Override
    protected void serviceStart() throws Exception {
        InetAddress localHost = InetAddress.getLocalHost();

        RPC.Server server = new RPC.Builder(conf)
                .setProtocol(Job.class)
                .setInstance(job)
                .setBindAddress(localHost.getHostAddress())
                .setPort(jstreamConf.getPort())
                .setVerbose(false)
                .build();

        server.start();

        super.serviceStart();
    }


}
