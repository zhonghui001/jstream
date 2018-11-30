package com.jyb.parser.antlr.tree;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ColumnFiled extends Statement {

    private final String field;

    public ColumnFiled(Optional<NodeLocation> location, String field) {
        super(location);
        this.field = field;
    }

    public String getField() {
        return field;
    }



    @Override
    public String toString() {
        return "ColumnAlias{" +
                "field='" + field + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnFiled that = (ColumnFiled) o;
        return Objects.equals(field, that.field);
    }

    @Override
    public List<? extends Node> getChildren() {
        return ImmutableList.of();
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
