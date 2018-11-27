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
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;
import java.util.Properties;

import static java.util.Objects.requireNonNull;


public class JStreamMain {


    public static void main(String[] args) {
        requireNonNull(args[0], "参数不能为null");
        requireNonNull(args[1], "参数不能为null");
        Properties properties = loadProperties(args[1]);
        requireNonNull(properties,"spark-submit args[1] 路径有问题，请查询");

        JstreamConfiguration conf = getJstreamConfiguration(args[0],properties);
        requireNonNull(conf, "JstreamConfiguration 不能为null");

        JstreamContext jstreamContext = new JstreamContext(conf);
        JStreamMain main = new JStreamMain(jstreamContext);
        main.execute(args[0], properties);
    }

    private static Properties loadProperties(String propertiesPath){
      try{
          Configuration conf = new Configuration();
          FileSystem fs = FileSystem.get(conf);
          FSDataInputStream in = fs.open(new Path(propertiesPath));
          Properties properties = new Properties();
          properties.load(in);
          return properties;
      }catch (Exception ex){
          ex.printStackTrace();
      }
      return null;
    }

    private static JstreamConfiguration getJstreamConfiguration(String jobId, Properties properties) {
        String sql = "select * from t_jstream_job where id=?";
        String url = properties.get("jdbc.url").toString();
        String username = properties.get("jdbc.username").toString();
        String password = properties.get("jdbc.password").toString();
        JobVo job = MysqlUtils.getJob(url, username, password, sql,
                Integer.parseInt(jobId.replace("JSTREAM_JOB_", "")));
        if (job == null) {
            throw new RuntimeException("jobid 为 " + jobId + " 在数据库中不存在");
        }

        return job.getConfiguration();

    }


    public JStreamMain(JstreamContext context) {
        this.context = context;
    }

    private JstreamContext context;


    public void execute(String jobId, Properties properties) {
        JstreamConfiguration conf = context.getConfiguration();
        //设置checkpoint 路径
        ExtConfig extConfig = conf.getExtConfig();
        extConfig.setSparkCheckPointPath(extConfig.getSparkCheckPointPath()+"/"+jobId);
        SparkSession spark = SparkSession.builder().appName(jobId).enableHiveSupport().getOrCreate();

        //监控
        spark.streams().addListener(new QueryLinstener());

        String applicationId = spark.sparkContext().applicationId();
        requireNonNull(applicationId,"applicationId 不能为null");
        updateApplicationId(jobId, applicationId, properties);

        //获取df
        JstreamSource source = SourceFactory.getSource(context);
        Dataset<Row> sdf = source.createStream(spark, context);



        //注册表
        sdf.createOrReplaceTempView("topic");

        //执行sql
        Dataset<Row> dfAfterSql = doSql(spark, conf.getSqlEntryList());
        requireNonNull(dfAfterSql, "sql list最后一个sql不能带别名");




        //执行sink
        JstreamSink sink = SinkFactory.getSink(context);
        sink.writeToSink(jobId, dfAfterSql, context);



    }

    private void updateApplicationId(String jobId, String applicationId,Properties properties) {
        String sql = "update t_jstream_job set applicationId=? where id=?";
        String url = properties.get("jdbc.url").toString();
        String username = properties.get("jdbc.username").toString();
        String password = properties.get("jdbc.password").toString();
        System.out.println("更新mysql");
        MysqlUtils.executeQuery(url, username, password, sql, applicationId,
                Integer.parseInt(jobId.replace("JSTREAM_JOB_", "")));
    }

    private Dataset<Row> doSql(SparkSession spark, List<SqlEntry> sqlEntryList) {

        Dataset<Row> df = null;
        for (SqlEntry sqlEntry : sqlEntryList) {
            String alias = sqlEntry.getAlias();
            String sql = sqlEntry.getSql();
            if (StringUtils.isNotBlank(alias)) {
                spark.sql(sql).createOrReplaceTempView(alias);
            } else {
                df = spark.sql(sql);
            }
        }

        return df;
    }
}
