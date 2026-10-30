package edu.transit;

/** Calculates a simple demonstration maintenance budget. */
public final class MaintenanceVisitor extends WalkingVisitor<Double> {

    @Override
    protected Double combine(ContainerNode node) {
        double total = 0.0;
        for (TransitNode child : node.children()) {
            total += child.accept(this);
        }
        return total;
    }

    @Override
    public Double visitBus(Bus bus) {
        return bus.fuelLitres() * 2.0;
    }

    @Override
    public Double visitElectricBus(ElectricBus n) {
        return n.batteryKwh() * 0.25;
    }
    @Override
    public Double visitTram(Tram tram) {
        return tram.energyKwh() * 0.5;
    }

    @Override
    public Double visitStation(Station station) {
        return 10.0;
    }

    @Override
    public Double visitTicketMachine(TicketMachine machine) {
        return 5.0;
    }
}
