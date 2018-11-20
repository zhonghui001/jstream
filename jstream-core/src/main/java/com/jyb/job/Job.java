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

    public String saveJob(JstreamConfiguration jstreamConfiguration);

    public void updateJob(JstreamConfiguration jstreamConfiguration,String jobId);

    public JobVoList listJobs();

    public void startJob(String jobId);

    public boolean killJob(String jobId);

    public boolean delJob(String jobId);

    public JobVo getJob(String jobId);

}
