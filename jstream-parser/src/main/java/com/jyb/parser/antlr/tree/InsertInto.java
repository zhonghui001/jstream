/*
 * Copyright (C) 2018 The Sylph Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jyb.parser.antlr.tree;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InsertInto
        extends Statement
{
    private final QualifiedName qualifiedName;
    private final String query;
    private ColumnAlias columnAlias;

    public InsertInto(NodeLocation location, QualifiedName qualifiedName,String query,ColumnAlias columnAlias)
    {
        super(Optional.of(location));
        this.qualifiedName = qualifiedName;
        this.query = query;
        this.columnAlias = columnAlias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsertInto that = (InsertInto) o;
        return Objects.equals(qualifiedName, that.qualifiedName) &&
                Objects.equals(query, that.query) &&
                Objects.equals(columnAlias, that.columnAlias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, query, columnAlias);
    }

    public QualifiedName getQualifiedName() {
        return qualifiedName;
    }

    public String getQuery() {
        return query;
    }

    public ColumnAlias getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(ColumnAlias columnAlias) {
        this.columnAlias = columnAlias;
    }

    @Override
    public List<? extends Node> getChildren()
    {
        return ImmutableList.of();
    }


    @Override
    public String toString() {
        return "InsertInto{" +
                "qualifiedName=" + qualifiedName +
                ", query='" + query + '\'' +
                ", columnAlias=" + columnAlias +
                '}';
    }
}
