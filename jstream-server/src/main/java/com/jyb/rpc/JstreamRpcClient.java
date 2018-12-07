package com.jyb.rpc;

import com.jyb.config.JstreamConfiguration;
import com.jyb.config.OutPutModeConfig;
import com.jyb.config.TriggerConfig;
import com.jyb.db.JobStreamService;
import com.jyb.job.Job;
import com.jyb.job.vo.JobVo;
import com.jyb.job.vo.JobVoList;
import com.jyb.sink.ConsoleSink;
import jyb.test.MockConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.RPC;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.jar.JarOutputStream;

/**
 * 测试用
 * 废弃
 */
public class JstreamRpcClient {

    public void call() throws Exception {
        Configuration conf = new Configuration();
        InetSocketAddress sock = new InetSocketAddress(InetAddress.getLocalHost(), 5980);
        Job proxy = RPC.getProxy(Job.class, 1L, sock, conf);
        //JstreamConfiguration conf2 = MockConfiguration.newInstance();

        //String jobId = proxy.saveJob(conf2);
        String jobId = proxy
                .saveJob("with resources(app_name='myfirstapp',resource_master='yarn-client',resource_driver_memory='10g',resource_executor_memory='5g',resource_driver_cores='2',resource_num_executor='100');" +
                                "create source table topic1( _topic varchar) with( type = 'kafka',source_kafka_server='',source_kafka_topic='ass') watermark('niu','10 mini');" +
                                "create sink table sink1(value varchar) with(type='kafka',sink_kafka_server='192.179.9',sink_kafka_topic='ass');" +
                                "with topic2 as(select * from topic1 where id=10);" +
                                "with (out_mode='append',trigger_processtime='10 minit') insert into sink1 select count(1) from topic2"
                        );

        //proxy.saveJob("create sink table topic2(value varchar) with(type='kafka',sink_kafka_server='192.179.9',sink_kafka_topic='ass')","678");

//        proxy.startJob(jobId);
//
//        JobVoList jobVoList = proxy.listJobs();
//
//        jobVoList.getJobVos().stream().forEach(
//                jobVo -> System.out.println(jobVo.getId()+" -- "+ jobVo.getApplicationId()+"  "+
//                        jobVo.getApplicationState())
//                );

    }

    public static void main(String[] args) throws Exception {
        JstreamRpcClient client = new JstreamRpcClient();
        client.call();
    }


}
