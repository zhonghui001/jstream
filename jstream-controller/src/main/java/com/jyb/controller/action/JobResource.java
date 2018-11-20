package com.jyb.controller.action;

import com.google.common.collect.ImmutableMap;
import com.jyb.config.JstreamConfiguration;
import com.jyb.controller.utils.JobProxy;
import com.jyb.job.Job;
import com.jyb.job.vo.JobVo;
import com.jyb.job.vo.JobVoList;
import com.jyb.vo.JobModel;
import com.jyb.vo.JstreamConfigutationUtils;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.*;

@javax.inject.Singleton
@Path("/job")
public class JobResource {

    public static final String JOB_PIRFIX="JSTREAM_JOB_";

    Job job;

    public JobResource(){
        job = JobProxy.newInstance();
    }



    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Path("save")
    @Produces({MediaType.APPLICATION_JSON})
    public Map save(@Context HttpServletRequest request,
                   @BeanParam JobModel jobModel
                    ){
        try{
            requireNonNull(jobModel,"save post信息为null");
            JstreamConfiguration configuration = JstreamConfigutationUtils.convert(jobModel);
            if (StringUtils.isNotEmpty(jobModel.getId())){
                job.updateJob(configuration,JOB_PIRFIX+jobModel.getId());
            }else{
                job.saveJob(configuration);
            }

            return ImmutableMap.of("data","sucess");
        }catch (Exception ex){
            ex.printStackTrace();
            return ImmutableMap.of("data", ex.getMessage());
        }
    }


    @GET
    @Path("list")
    @Produces({MediaType.APPLICATION_JSON})
    public Map list(){
       try{
           JobVoList jobVoList = job.listJobs();
           List<JobVo> jobVos = jobVoList.getJobVos();
           return ImmutableMap.of("data",jobVos);
       }catch (Exception ex){
           ex.printStackTrace();
           return ImmutableMap.of("data",ex.getMessage());
       }
    }

    @GET
    @Path("start")
    @Produces({MediaType.APPLICATION_JSON})
    public Map start(@QueryParam("jobId") String id){
        try{
            job.startJob(JOB_PIRFIX+id);
            return ImmutableMap.of("data","sucess");
        }catch (Exception ex){
            ex.printStackTrace();
            return ImmutableMap.of("data",ex.getMessage());
        }
    }

    @GET
    @Path("stop")
    @Produces({MediaType.APPLICATION_JSON})
    public Map stop(@QueryParam("jobId") String id){
        try{
            if (StringUtils.isEmpty(job.getJob(JOB_PIRFIX+id).getApplicationId())){
                throw new RuntimeException("streaming程序正在启动中，未获取到applicationid，请稍后再试！");
            }
            job.killJob(JOB_PIRFIX+id);
            return ImmutableMap.of("data","sucess");
        }catch (Exception ex){
            ex.printStackTrace();
            return ImmutableMap.of("data",ex.getMessage());
        }
    }


    @GET
    @Path("del")
    @Produces({MediaType.APPLICATION_JSON})
    public Map del(@QueryParam("jobId") String id){
        try{
            job.delJob(JOB_PIRFIX+id);
            return ImmutableMap.of("data","sucess");
        }catch (Exception ex){
            ex.printStackTrace();
            return ImmutableMap.of("data",ex.getMessage());
        }

    }


    @GET
    @Path("getJob")
    @Produces({MediaType.APPLICATION_JSON})
    public Map getJob(@QueryParam("jobId") String id){
        try{
            JobVo job = this.job.getJob(JOB_PIRFIX + id);
            JobModel model = JstreamConfigutationUtils.parse(job.getConfiguration());
            return ImmutableMap.of("data",model);
        }catch (Exception ex){
            ex.printStackTrace();
            return ImmutableMap.of("data",ex.getMessage());
        }

    }



}
