package com.jyb.config;

import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class JstreamConfiguration implements Serializable, Writable {


    private static final long serialVersionUID = 7836737459382017737L;

    //资源类配置
    private ResouceConfig resouceConfig;

    //额外的一些配置 如appname
    private ExtConfig extConfig;

    private Config sourceConfig;
    private Config sinkConfig;



    private List<SqlEntry> sqlEntryList = new LinkedList<SqlEntry>() ;

    public JstreamConfiguration(){}

    public JstreamConfiguration(ResouceConfig resouceConfig, ExtConfig extConfig, Config sourceConfig, Config sinkConfig) {
        this.resouceConfig = requireNonNull(resouceConfig,"resouceConfig 必须不为null");
        this.extConfig = requireNonNull(extConfig,"extConfig必须不为null");
        this.sourceConfig = requireNonNull(sourceConfig,"sourceConfig必须不为null");
        this.sinkConfig = requireNonNull(sinkConfig,"sinkConfig必须不为null");
    }

    public JstreamConfiguration addSqlEntry(SqlEntry sqlEntry) {
        sqlEntryList.add(sqlEntry);
        return this;
    }

    public List<SqlEntry> getSqlEntryList() {
        return sqlEntryList;
    }

    public void setSqlEntryList(List<SqlEntry> sqlEntryList) {
        this.sqlEntryList = sqlEntryList;
    }

    public ResouceConfig getResouceConfig() {
        return resouceConfig;
    }

    public void setResouceConfig(ResouceConfig resouceConfig) {
        this.resouceConfig = resouceConfig;
    }

    public ExtConfig getExtConfig() {
        return extConfig;
    }

    public void setExtConfig(ExtConfig extConfig) {
        this.extConfig = extConfig;
    }

    public Config getSourceConfig() {
        return sourceConfig;
    }

    public void setSourceConfig(Config sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public Config getSinkConfig() {
        return sinkConfig;
    }

    public void setSinkConfig(Config sinkConfig) {
        this.sinkConfig = sinkConfig;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        ObjectWritable.writeObject(out,resouceConfig, resouceConfig.getClass(),null);
        ObjectWritable.writeObject(out,extConfig,extConfig.getClass(),null);
        ObjectWritable.writeObject(out,sourceConfig,sinkConfig.getClass(),null);
        ObjectWritable.writeObject(out,sinkConfig,sinkConfig.getClass(),null);
        //ObjectWritable.writeObject(out,sqlEntryList,sqlEntryList.getClass(),null);
        SqlEntry[] en = sqlEntryList.toArray(new SqlEntry[]{});
        ObjectWritable.writeObject(out,en,en.getClass(),null);
        //WritableUtils.writeCompressedStringArray(out,sqlEntryList.toArray(new String[]{}));
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        resouceConfig = (ResouceConfig) ObjectWritable.readObject(in,null);
        extConfig = (ExtConfig) ObjectWritable.readObject(in,null);
        sourceConfig = (Config) ObjectWritable.readObject(in,null);
        sinkConfig = (Config) ObjectWritable.readObject(in,null);
        SqlEntry[] ens = (SqlEntry[]) ObjectWritable.readObject(in,null);
        //String[] sqls = WritableUtils.readCompressedStringArray(in);
        Arrays.stream(ens).forEach(sqlEntryList::add);

    }
}
