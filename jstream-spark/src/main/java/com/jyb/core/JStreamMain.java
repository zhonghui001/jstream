package com.jyb.core;

import com.jyb.config.ExtConfig;
import com.jyb.config.JstreamConfiguration;
import com.jyb.config.JstreamContext;
import com.jyb.config.SqlEntry;

import com.jyb.jdbc.MysqlUtils;
import com.jyb.job.vo.JobVo;
import com.jyb.sink.JstreamSink;
import com.jyb.sink.SinkFactory;
import com.jyb.source.JstreamSource;
import com.jyb.source.SourceFactory;
import com.jyb.util.ConfigutionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkFiles;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static java.util.Objects.requireNonNull;


public class JStreamMain {



    public static void main(String[] args) {
        requireNonNull(args[0],"参数不能为null");
        JstreamConfiguration conf = getJstreamConfiguration(args[0]);
        requireNonNull(conf,"JstreamConfiguration 不能为null");
        JstreamContext jstreamContext = new JstreamContext(conf);
        JStreamMain main = new JStreamMain(jstreamContext);
        main.execute(args[0]);
    }

    private static JstreamConfiguration getJstreamConfiguration(String jobId){
        try{
            String sql="select * from t_jstream_job where id=?";
            System.out.println("jstream home为 ： "+System.getProperty("JSTREAM_HOME"));
            String path = System.getProperty("JSTREAM_HOME","/data/jstream")+"/conf/jstream.properties";
            File configFile = new File(path);
            Properties properties = new Properties();
            properties.load(new FileInputStream(configFile));
            String url=properties.get("jdbc.url").toString();
            String username=properties.get("jdbc.username").toString();
            String password=properties.get("jdbc.password").toString();
            System.out.println("jstream.properties的url为 "+url);
            JobVo job = MysqlUtils.getJob(url, username, password, sql,
                    Integer.parseInt(jobId.replace("JSTREAM_JOB_", "")));
            if (job == null){
                throw new RuntimeException("jobid 为 "+jobId+" 在数据库中不存在");
            }

            System.out.println("job :"+job +" job.configuration :"+job.getConfiguration()+" job.name:"+job.getApplicationState());

            return job.getConfiguration();
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }

        return null;

    }




    public JStreamMain(JstreamContext context) {
        this.context = context;
    }

    private JstreamContext context;



    public void execute(String jobId){
        JstreamConfiguration conf = context.getConfiguration();
        ExtConfig extConfig = conf.getExtConfig();
        SparkSession spark = SparkSession.builder().appName(jobId).enableHiveSupport().getOrCreate();

        //获取df
        JstreamSource source = SourceFactory.getSource(context);
        Dataset<Row> sdf = source.createStream(spark, context);

        //注册表
        sdf.createOrReplaceTempView("topic");

        //执行sql
        Dataset<Row> dfAfterSql = doSql(spark, conf.getSqlEntryList());
        requireNonNull(dfAfterSql,"sql list最后一个sql不能带别名");


        String applicationId = spark.sparkContext().applicationId();
        updateApplicationId(jobId,applicationId);

        //执行sink
        JstreamSink sink = SinkFactory.getSink(context);
        sink.writeToSink(dfAfterSql,context);



    }

    private void updateApplicationId(String jobId,String applicationId){
        String sql="update t_jstream_job set applicationId=? where id=?";
       try{
           File configFile = new File("/data/jstream/conf/jstream.properties");
           Properties properties = new Properties();
           properties.load(new FileInputStream(configFile));
           String url=properties.get("jdbc.url").toString();
           String username=properties.get("jdbc.username").toString();
           String password=properties.get("jdbc.password").toString();
           MysqlUtils.executeQuery(url,username,password,sql,applicationId,
                   Integer.parseInt(jobId.replace("JSTREAM_JOB_", "")));
       }catch (Exception ex){
           ex.printStackTrace();
       }
    }

    private Dataset<Row> doSql(SparkSession spark, List<SqlEntry> sqlEntryList){

        Dataset<Row> df=null;
        for (SqlEntry sqlEntry:sqlEntryList){
            String alias = sqlEntry.getAlias();
            String sql = sqlEntry.getSql();
            if (StringUtils.isNotBlank(alias)){
                spark.sql(sql).createOrReplaceTempView(alias);
            }else{
                df = spark.sql(sql);
            }
        }

        return df;
    }
}
