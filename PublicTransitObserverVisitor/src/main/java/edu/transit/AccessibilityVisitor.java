package edu.transit;

import java.util.*;


public final class AccessibilityVisitor extends WalkingVisitor<List<TransitNode >> {
    protected List<TransitNode>combine(ContainerNode n) {
        List<TransitNode>out = new ArrayList<>();
        for (var c: n.children()) out.addAll(c.accept(this));
        return out;
    }
    private List<TransitNode>filter(TransitNode n, boolean accessible) {
        return accessible ? List.of(n): List.of();
    }
    public List<TransitNode>visitBus(Bus n) {
        return filter(n, n.accessible());
    }
    public List<TransitNode>visitTram(Tram n) {
        return filter(n, n.accessible());
    }
    public List<TransitNode>visitStation(Station n) {
        return filter(n, n.accessible());
    }
    public List<TransitNode>visitTicketMachine(TicketMachine n) {
        return filter(n, n.accessible());
    }
}
