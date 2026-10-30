package edu.transit;

public record Station(String id, boolean accessible, double maintenanceCost) implements TransitNode {
    public < R>R accept(TransitVisitor<R>v) {
        return v.visitStation(this);
    }
}
