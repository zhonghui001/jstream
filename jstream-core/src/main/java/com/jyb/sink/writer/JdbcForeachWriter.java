package com.jyb.sink.writer;

import com.jyb.jdbc.ConnectionUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.*;
import scala.collection.Iterator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class JdbcForeachWriter extends AbstractWritter implements Serializable {

    String url;
    String userName;
    String password;
    String tableName;

    public JdbcForeachWriter(String url, String userName, String password, String tableName) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.tableName = tableName;
    }

    Connection con;

    @Override
    public boolean open(long partitionId, long version) {
        try {
            con = ConnectionUtils.getMysqlConnection(url,userName,password);
            return con != null ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void process(Row row) {
        StringBuilder sb = new StringBuilder("replace into " + tableName + "  values (");

        Iterator<StructField> it = row.schema().iterator();
        int i=0;
        while (it.hasNext()){
            StructField sf = it.next();
            if (sf.dataType() instanceof StructType){
                Row st = row.getStruct(i++);
                Timestamp begin = st.getTimestamp(0);
                Timestamp end = st.getTimestamp(1);
                String tmp = begin + "-" + end;
                sb.append("'"+tmp+"',");
            }else if(sf.dataType() instanceof DoubleType){
                sb.append(row.getDouble(i++)+",");
            }else if(sf.dataType() instanceof IntegerType){
                sb.append(row.getInt(i++)+",");
            }else if (sf.dataType() instanceof LongType){
                sb.append(row.getLong(i++)+",");
            }else if(sf.dataType() instanceof NullType){
                sb.append(null+",");
            }else if(sf.dataType() instanceof StringType){
                sb.append("'"+row.getString(i++)+"',");
            }else if (sf.dataType() instanceof TimestampType){
                sb.append("'"+row.getTimestamp(i++)+',');
            }
        }


        String sql = sb.toString().substring(0,sb.toString().lastIndexOf(","))+")";
        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.execute(con, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close(Throwable errorOrNull) {
        try {
            if (!con.isClosed()){
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

