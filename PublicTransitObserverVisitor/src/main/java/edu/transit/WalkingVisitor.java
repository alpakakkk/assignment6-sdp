package edu.transit;

// Visitors own traversal; elements contain only data and accept()/dispatch.
public abstract class WalkingVisitor<R>implements TransitVisitor<R> {
    protected abstract R combine(ContainerNode node);
    public R visitNetwork(TransitNetwork n) {
        return combine(n);
    }
    public R visitRoute(TransitRoute n) {
        return combine(n);
    }
    public R visitStop(TransitStop n) {
        return combine(n);
    }
}
