package edu.transit;

import java.util.*;


public record TransitStop(String id, List<TransitNode>children) implements ContainerNode {
    public TransitStop {
        children = List.copyOf(children);
    }
    public < R>R accept(TransitVisitor<R>v) {
        return v.visitStop(this);
    }
}
