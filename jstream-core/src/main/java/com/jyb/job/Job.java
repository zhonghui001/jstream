package com.jyb.job;

import com.jyb.config.JstreamConfiguration;
import com.jyb.job.vo.JobVo;
import com.jyb.job.vo.JobVoList;
import org.apache.hadoop.ipc.VersionedProtocol;

/**
 * 管理 job的生命周期
 */
public interface Job extends VersionedProtocol {

    public static final long versionID = 1L;

    default public String saveJob(JstreamConfiguration jstreamConfiguration,String sql){
        throw new RuntimeException("不支持该方法，该方法是v1.0版本");
    }

    public void updateJob(JstreamConfiguration jstreamConfiguration,String jobId,String sql);

    public JobVoList listJobs();

    public void startJob(String jobId);

    public boolean killJob(String jobId);

    public boolean delJob(String jobId);

    public JobVo getJob(String jobId);

    public void updateJob(String id,String sql);
    default public String saveJob(String sql){
        throw new RuntimeException("不支持该方法，该方法是v2.0版本");
    }

}
