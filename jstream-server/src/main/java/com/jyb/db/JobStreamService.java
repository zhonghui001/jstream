package com.jyb.db;

import com.jyb.config.JstreamConfiguration;
import com.jyb.jdbc.MysqlUtils;
import com.jyb.job.JobStateMachine;
import com.jyb.job.vo.JobVo;
import com.jyb.jstream.config.JstreamConf;

import javax.inject.Inject;
import java.util.List;

/**
 * 与数据库操作
 */
public class JobStreamService {

    JstreamConf jstreamConf;

    public static final String JOB_PIRFIX="JSTREAM_JOB_";

    @Inject
    public JobStreamService(JstreamConf jstreamConf){
        this.jstreamConf = jstreamConf;
    }


    public String saveJob(JobVo jobvo){
        String sql="insert into t_jstream_job (configuration,jobState,appName,sqlStr) values(?,?,?,?)";
        Integer id = MysqlUtils.insert(jstreamConf, sql, jobvo.getConfiguration(),
                JobStateMachine.JobStateInternal.NEW.toString(),
                jobvo.getConfiguration().getExtConfig().getAppName(),
                jobvo.getSqlStr());
        return "JSTREAM_JOB_"+id;
    }

    public void updateJobConfiguration(JstreamConfiguration jstreamConfiguration, String jobId,String sqlStr){
        String sql = "update t_jstream_job set configuration=? ,appName=?,sqlStr=? where id=? ";
        MysqlUtils.executeQuery(jstreamConf,sql,jstreamConfiguration,
                jstreamConfiguration.getExtConfig().getAppName(),
                sqlStr,
                Integer.parseInt(jobId.replace(JOB_PIRFIX,"")));
    }

    public void updateJobState(String jobId,JobStateMachine.JobStateInternal state){
        String sql = "update t_jstream_job set jobState=? where id=?";
        MysqlUtils.executeQuery(jstreamConf,sql,
                state.toString(),
                Integer.parseInt(jobId.replace(JOB_PIRFIX,"")));
    }

    public void cleanJobApplicationId(String jobId){
        String sql = "update t_jstream_job set applicationId= null where id=? ";
        MysqlUtils.executeQuery(jstreamConf,sql,
                Integer.parseInt(jobId.replace(JOB_PIRFIX,"")));
    }

    public List<JobVo> list(){
        return MysqlUtils.listJobs(jstreamConf.getJdbcUrl(),
                jstreamConf.getJdbcUsername(),
                jstreamConf.getJdbcPassword(),
                "select * from t_jstream_job order by id desc");
    }

    public JobVo getJob(String jobId){
        return MysqlUtils.getJob(jstreamConf,
                "select * from t_jstream_job where id = ?",
                jobId.replace("JSTREAM_JOB_","")
                );
    }

    public void delJob(String jobId){
        MysqlUtils.executeQuery(jstreamConf,
                "delete from t_jstream_job where id = ?",
                jobId.replace("JSTREAM_JOB_","")
        );
    }







}
