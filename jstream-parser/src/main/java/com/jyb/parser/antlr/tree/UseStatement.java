package com.jyb.parser.antlr.tree;

import java.util.List;
import java.util.Optional;

public class UseStatement extends Statement {
    Identifier schema;
    public UseStatement(NodeLocation location,Identifier schema) {
        super(Optional.of(location));
        this.schema = schema;
    }

    @Override
    public List<? extends Node> getChildren() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public String toString() {
        return "UseStatement{" +
                "schema=" + schema +
                '}';
    }
}
