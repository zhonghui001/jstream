package com.jyb.shell;


import org.apache.commons.crypto.utils.IoUtils;
import org.apache.commons.exec.*;
import org.apache.commons.io.IOUtils;

import java.io.*;


public class Shell {

    String outPath;
    String errPath;


    public Shell(String outPath,String errPath){
        this.outPath = outPath;
        this.errPath = errPath;
    }


    public void execute(String line)throws Exception {

        CommandLine cmdLine = CommandLine.parse(line);

        DefaultExecutor executor = new DefaultExecutor();
        FileOutputStream outputStream = new FileOutputStream(new File(outPath));
        FileOutputStream errStream = new FileOutputStream(new File(errPath));



        executor.setStreamHandler(new PumpStreamHandler(outputStream,errStream));

        executor.setExitValue(0);
        //ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
        //executor.setWatchdog(watchdog);
        int exitValue = executor.execute(cmdLine);

    }




    public static void main(String[] args) throws Exception {
        Shell shell = new Shell("/home/zh/Desktop/png/out.data", "/home/zh/Desktop/png/error.data");
        shell.execute("/data/spark/bin/spark-submit --master yarn-client  --jars /data/jstream/lib/commons-dbutils-1.7.jar,/data/jstream/lib/jstream-core-1.0-SNAPSHOT.jar,/data/jstream/lib/mysql-connector-java-5.1.45.jar --files /data/jstream/conf/jstream.properties  --class com.jyb.core.JStreamMain /data/jstream/lib/jstream-spark-1.0-SNAPSHOT.jar JSTREAM_JOB_31");

    }
}
