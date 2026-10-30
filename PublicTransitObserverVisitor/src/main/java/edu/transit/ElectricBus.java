package edu.transit;

/** Immutable new leaf type added after the visitor interfaces were established. */
public record ElectricBus(String id, double batteryKwh, int seats, boolean accessible)
        implements TransitNode {

    @Override
    public <R> R accept(TransitVisitor<R> visitor) {
        return visitor.visitElectricBus(this);
    }
}
