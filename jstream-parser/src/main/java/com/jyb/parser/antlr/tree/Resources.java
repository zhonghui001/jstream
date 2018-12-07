package com.jyb.parser.antlr.tree;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Resources extends Statement {

    private final List<Property> properties;

    public Resources(NodeLocation location, List<Property> properties) {
        super(Optional.of(location));
        this.properties = properties;
    }

    public Resources(List<Property> properties, Optional<NodeLocation> location) {
        super(location);
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public List<Node> getChildren()
    {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return "Resources{" +
                "properties=" + properties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resources resources = (Resources) o;
        return Objects.equals(properties, resources.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }
}
