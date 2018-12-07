package com.jyb.vo;

import javax.ws.rs.FormParam;

public class JobModelV2 {

    @FormParam("id")
    private String id;
    @FormParam("sql")
    private String sql;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
