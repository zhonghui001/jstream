package com.jyb.sink.writer;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.*;
import redis.clients.jedis.Jedis;
import scala.collection.Iterator;

import java.io.Serializable;
import java.sql.Timestamp;

import static java.util.Objects.*;

public class RedisForeachWritter extends AbstractWritter implements Serializable {
    String host;
    Integer port=6379;
    Integer dbNo;
    String redisKey;

    Jedis jedis;


    public RedisForeachWritter(String host, String redisKey){
        this(host,6379,0,redisKey);
    }

    public RedisForeachWritter(String host, Integer port, Integer dbNo, String redisKey) {
        this.host = requireNonNull(host,"redis host不能为null");
        if (port==null)
            port=6379;
        if (dbNo==null)
            dbNo=0;
        this.dbNo = requireNonNull(dbNo,"redis 数据库不能为null");
        this.port = requireNonNull(port,"redis 端口不能为null");
        this.redisKey = requireNonNull(redisKey,"jobid不能为null");
    }


    @Override
    public boolean open(long partitionId, long version) {
        assert StringUtils.isNotBlank(host);
        assert port!=null;
        jedis = new Jedis(host, port);
        jedis.select(dbNo);
        return true;
    }

    @Override
    public void process(Row row) {
        Iterator<StructField> it = row.schema().iterator();
        int i=0;
        String realKey=redisKey;
        if (StringUtils.contains(redisKey,"$")){
             realKey = getRealKey(row);
        }

        while (it.hasNext()){
            StructField sf = it.next();
            if (sf.dataType() instanceof StructType){
                Row st = row.getStruct(i++);
                Timestamp begin = st.getTimestamp(0);
                Timestamp end = st.getTimestamp(1);
                String tmp = begin + "-" + end;
                jedis.hset(realKey,sf.name(),tmp);
            }else if(sf.dataType() instanceof NullType){

            }else{
                jedis.hset(realKey,sf.name(),String.valueOf(row.get(i++)));
            }
        }
    }

    private String getRealKey(Row row){
        Iterator<StructField> it = row.schema().iterator();
        int i=0;
        while (it.hasNext()){
            StructField sf = it.next();
            String value="";
            if (StringUtils.contains(redisKey,"$"+sf.name())){
                if (sf.dataType() instanceof StructType){
                    Row st = row.getStruct(i++);
                    Timestamp begin = st.getTimestamp(0);
                    Timestamp end = st.getTimestamp(1);
                    value= begin + "-" + end;
                }else if(sf.dataType() instanceof NullType){

                }else{
                    value = row.get(i++).toString();
                }
                return StringUtils.replace(redisKey,"$"+sf.name(),value);
            }else{
                i++;
            }
        }
        return null;
    }

    @Override
    public void close(Throwable errorOrNull) {
        jedis.close();
    }
}
