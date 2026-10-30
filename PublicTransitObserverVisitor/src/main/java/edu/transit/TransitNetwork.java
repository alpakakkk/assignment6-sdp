package edu.transit;

import java.util.*;


public record TransitNetwork(String id, List<TransitNode>children) implements ContainerNode {
    public TransitNetwork {
        children = List.copyOf(children);
    }
    public < R>R accept(TransitVisitor<R>v) {
        return v.visitNetwork(this);
    }
}
