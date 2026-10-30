package edu.transit;

import java.util.*;


public final class InventoryVisitor extends WalkingVisitor<Void> {
    private final Map<String, Integer>counts = new LinkedHashMap<>();
    protected Void combine(ContainerNode n) {
        counts.merge(n.getClass().getSimpleName(), 1, Integer::sum);
        for (var c: n.children()) c.accept(this);
        return null;
    }
    private Void leaf(TransitNode n) {
        counts.merge(n.getClass().getSimpleName(), 1, Integer::sum);
        return null;
    }
    public Void visitBus(Bus n) {
        return leaf(n);
    }
    public Void visitTram(Tram n) {
        return leaf(n);
    }
    public Void visitStation(Station n) {
        return leaf(n);
    }
    public Void visitTicketMachine(TicketMachine n) {
        return leaf(n);
    }
    public Map<String, Integer>snapshot() {
        return Map.copyOf(counts);
    }
}
