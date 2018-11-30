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

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class WaterMark
        extends Node
{

    private String filedName;
    private String expression;

    public WaterMark(NodeLocation location, String filedName, String expression)
    {
        super(Optional.of(location));
        this.filedName = requireNonNull(filedName);
        this.expression = requireNonNull(expression);
    }



    @Override
    public List<? extends Node> getChildren()
    {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return "WaterMark{" +
                "filedName='" + filedName + '\'' +
                ", expression='" + expression + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterMark waterMark = (WaterMark) o;
        return Objects.equals(filedName, waterMark.filedName) &&
                Objects.equals(expression, waterMark.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filedName, expression);
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
