package edu.transit;

public interface TransitVisitor<R> {
    R visitNetwork(TransitNetwork n);
    R visitRoute(TransitRoute n);
    R visitStop(TransitStop n);
    R visitBus(Bus n);
    R visitElectricBus(ElectricBus n);
    R visitTram(Tram n);
    R visitStation(Station n);
    R visitTicketMachine(TicketMachine n);
}
