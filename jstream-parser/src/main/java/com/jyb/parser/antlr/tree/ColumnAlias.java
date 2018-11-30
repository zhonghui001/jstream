package com.jyb.parser.antlr.tree;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ColumnAlias extends Statement {

    private List<ColumnFiled> fileds;
    public ColumnAlias(NodeLocation location,List<ColumnFiled> fileds) {
        super(Optional.of(location));
        this.fileds = fileds;
    }

    @Override
    public List<? extends Node> getChildren() {
        return null;
    }

    public List<ColumnFiled> getFileds() {
        return fileds;
    }

    public void setFileds(List<ColumnFiled> fileds) {
        this.fileds = fileds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnAlias that = (ColumnAlias) o;
        return Objects.equals(fileds, that.fileds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileds);
    }

    @Override
    public String toString() {
        return "ColumnAlias{" +
                "fileds=" + fileds +
                '}';
    }
}
