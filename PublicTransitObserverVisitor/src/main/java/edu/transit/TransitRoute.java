package edu.transit;

import java.util.*;


public record TransitRoute(String id, List<TransitNode>children) implements ContainerNode {
    public TransitRoute {
        children = List.copyOf(children);
    }
    public < R>R accept(TransitVisitor<R>v) {
        return v.visitRoute(this);
    }
}
