package com.jyb.config;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import static java.util.Objects.*;
/**
 * sql集合
 */
public class SqlEntry implements Serializable, Writable {

    private static final long serialVersionUID = -8316883546379762446L;
    String sql="";
    String alias="";

    public SqlEntry() {
    }

    public SqlEntry(String sql, String alias) {
        this.sql = requireNonNull(sql,"sql不能为null");
        this.alias = requireNonNull(alias,"alias不能为努力了,可以用‘’代替");
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(sql);
        out.writeUTF(alias);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        sql = in.readUTF();
        alias = in.readUTF();
    }
}
