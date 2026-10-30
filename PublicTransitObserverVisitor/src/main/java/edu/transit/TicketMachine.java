package edu.transit;

public record TicketMachine(String id, long ticketsSold, double ticketPrice, boolean accessible) implements TransitNode {
    public < R>R accept(TransitVisitor<R>v) {
        return v.visitTicketMachine(this);
    }
}
