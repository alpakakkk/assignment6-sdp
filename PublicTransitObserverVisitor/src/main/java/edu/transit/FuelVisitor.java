package edu.transit;

public final class FuelVisitor extends WalkingVisitor<Double> {
    protected Double combine(ContainerNode n) {
        double sum = 0;
        for (var child: n.children()) sum += child.accept(this);
        return sum;
    }
    public Double visitBus(Bus n) {
        return n.fuelLitres();
    }
    public Double visitTram(Tram n) {
        return 0.0;
    }
    public Double visitStation(Station n) {
        return 0.0;
    }
    public Double visitTicketMachine(TicketMachine n) {
        return 0.0;
    }
}
