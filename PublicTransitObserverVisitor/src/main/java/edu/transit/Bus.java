package edu.transit;

public record Bus(String id, double fuelLitres, int seats, boolean accessible) implements TransitNode {
    public < R>R accept(TransitVisitor<R>v) {
        return v.visitBus(this);
    }
}
