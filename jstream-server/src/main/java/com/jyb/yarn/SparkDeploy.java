package com.jyb.yarn;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.client.api.YarnClient;

/**
 * 将spark jar提交至yarn中
 */
public class SparkDeploy {

    String confPath;
    String jarPath;

    public SparkDeploy(String confPath,String jarPath){
        this.confPath = confPath;
        this.jarPath = jarPath;
    }

    public void submit(){
        //YarnClient
    }
}
