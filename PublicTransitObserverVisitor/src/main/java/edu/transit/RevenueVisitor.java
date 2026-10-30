package edu.transit;

public final class RevenueVisitor extends WalkingVisitor<Double> {
    protected Double combine(ContainerNode n) {
        double sum = 0;
        for (var child: n.children()) sum += child.accept(this);
        return sum;
    }
    public Double visitBus(Bus n) {
        return 0.0;
    }
    @Override
    public Double visitElectricBus(ElectricBus n) {
        return 0.0;
    }
    public Double visitTram(Tram n) {
        return 0.0;
    }
    public Double visitStation(Station n) {
        return 0.0;
    }
    public Double visitTicketMachine(TicketMachine n) {
        return n.ticketsSold() * n.ticketPrice();
    }
}
