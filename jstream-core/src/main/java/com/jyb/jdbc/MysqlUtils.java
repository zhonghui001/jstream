package com.jyb.jdbc;

import com.jyb.config.JstreamConfiguration;
import com.jyb.job.vo.JobVo;
import com.jyb.jstream.config.JstreamConf;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysqlUtils {

    public static Integer insert(JstreamConf conf,String sql,Object ... params){
        QueryRunner query = new QueryRunner();
        Connection con = ConnectionUtils.getMysqlCOnnection(conf);
        try{
            return query.insert(con, sql, new ResultSetHandler<Integer>() {
                @Override
                public Integer handle(ResultSet resultSet) throws SQLException {
                    while (resultSet.next()){
                        return resultSet.getInt(1);
                    }
                    return 0;
                }
            },params);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void executeQuery(JstreamConf conf,String sql,Object ... params){
        executeQuery(conf.getJdbcUrl(),conf.getJdbcUsername(),conf.getJdbcPassword(),sql,params);
    }

    public static JobVo getJob(JstreamConf conf,String sql,Object ... params){
        return getJob(conf.getJdbcUrl(),conf.getJdbcUsername(),conf.getJdbcPassword(),sql,params);
    }

    public static JobVo getJob(String url,String userName,String password,String sql,Object ... params){
        List<JobVo> jobVos = listJobs(url, userName, password, sql, params);
        return jobVos.size()>0?jobVos.get(0):null;
    }


    public static List<JobVo> listJobs(String url,String userName,String password,String sql,Object ... params){
        QueryRunner query = new QueryRunner();
        Connection con = ConnectionUtils.getMysqlConnection(url,userName,password);
        try {
            List<List<JobVo>> result = query.execute(con, sql, new ResultSetHandler<List<JobVo>>() {
                @Override
                public List<JobVo> handle(ResultSet resultSet) throws SQLException {
                    List<JobVo> jobVos = new ArrayList<JobVo>();
                    while (resultSet.next()) {
                        JobVo jobVo = new JobVo();
                        jobVo.setId(resultSet.getInt(1));
                        jobVo.setAppName(resultSet.getString(2) == null ?"":resultSet.getString(2));
                        jobVo.setConfPath(resultSet.getString(3) == null ?"":resultSet.getString(3));
                        jobVo.setJobState(resultSet.getString(4) == null?"":resultSet.getString(4));
                        jobVo.setApplicationId(resultSet.getString(5) == null?"":resultSet.getString(5));
                        jobVo.setApplicationState(resultSet.getString(6)==null?"":resultSet.getString(6));

                        Blob blob = resultSet.getBlob(7);
                        InputStream is = blob.getBinaryStream();
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buff = new byte[(int) blob.length()];
                        try {
                            while (-1 != (bis.read(buff, 0, buff.length))) {
                                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buff));
                                JstreamConfiguration p = (JstreamConfiguration) in.readObject();
                                jobVo.setConfiguration(p);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        jobVos.add(jobVo);


                    }
                    return jobVos;
                }
            }, params);
            return result.size()>0? result.get(0):null;
            //return query.execute(con,sql,new BeanHandler<JobVo>(JobVo.class),params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void executeQuery(String url,String userName,String password,String sql,Object ... params){
        QueryRunner query = new QueryRunner();
        Connection con = ConnectionUtils.getMysqlConnection(url,userName,password);
        try {
            query.execute(con,sql,params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url="jdbc:mysql://localhost:3306/test";
        String userName="zh";
        String password="123456";
        String sql="select * from t_jstream_job";
        List<JobVo> jobVos = MysqlUtils.listJobs(url, userName, password, sql, null);
        System.out.println(jobVos.get(1).getConfiguration().getExtConfig().getAppName());
    }
}
