package edu.transit;

public interface TransitNode {
    String id();
    < R>R accept(TransitVisitor<R>visitor);
}
