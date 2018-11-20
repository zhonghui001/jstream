package com.jyb.sink.writer;

import com.alibaba.fastjson.JSON;
import org.apache.spark.sql.ForeachWriter;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;

import java.sql.Timestamp;
import java.util.HashMap;

public abstract class AbstractWritter  extends ForeachWriter<Row> {

    protected String keys;
    protected Object vals;

    protected void processKeyValues(Row row){
        Row st = row.getStruct(0);
        Timestamp begin = st.getTimestamp(0);
        Timestamp end = st.getTimestamp(1);
        keys = begin + "-" + end;

        StructType schema = row.schema();
        String[] names = schema.fieldNames();
        HashMap<String, Object> map = new HashMap<>();
        for (int i=1;i<names.length;i++){
            Object val = getVal(row, i);
            String name = names[i];
            map.put(name,val);
        }
        if (map.size()==1){
            vals=map.entrySet().iterator().next().getValue();
        }
        vals = JSON.toJSONString(map);

    }

    protected Object getVal(Row row,Integer i){
        Object val=null;
        try{
            val = row.getDouble(i);
        }catch (Exception ex){
            val = row.getLong(i);
        }
        return vals;
    }

}
