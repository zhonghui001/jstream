package com.jyb.jdbc;

import com.jyb.jstream.config.JstreamConf;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;


public class ConnectionUtils {

//    public static String hiveDirverName = "org.apache.hive.jdbc.HiveDriver";
    public static String mysqlDirverName = "com.mysql.jdbc.Driver";

    public static Connection getMysqlCOnnection(JstreamConf conf){
        if (!StringUtils.equals(conf.getJdbcType(),"mysql")){
            throw new RuntimeException("不支持数据类型"+conf.getJdbcType()+" 请修改jstream.properties中的jdbc.type为mysql");
        }
        return getMysqlConnection(conf.getJdbcUrl(),conf.getJdbcUsername(),conf.getJdbcPassword());
    }


    public static Connection getMysqlConnection(String jdbcUrl,
                                                String userName, String pass) {
        Connection conn = null;
        try {
            Class.forName(mysqlDirverName);
            conn = DriverManager.getConnection(jdbcUrl, userName, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }


}
