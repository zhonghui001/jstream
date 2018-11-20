package com.jyb.controller.utils;

import com.jyb.job.Job;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class JobProxy {

    public static Job newInstance(){
       try{
           Configuration conf = new Configuration();
           InetSocketAddress sock = new InetSocketAddress(InetAddress.getLocalHost(), 5980);
           Job proxy = RPC.getProxy(Job.class, 1L, sock, conf);
           return proxy;
       }catch (Exception ex){
           ex.printStackTrace();
           return null;
       }
    }

}
