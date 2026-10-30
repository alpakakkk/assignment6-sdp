package edu.transit;

public record Tram(String id, double energyKwh, int seats, boolean accessible) implements TransitNode {
    public < R>R accept(TransitVisitor<R>v) {
        return v.visitTram(this);
    }
}
