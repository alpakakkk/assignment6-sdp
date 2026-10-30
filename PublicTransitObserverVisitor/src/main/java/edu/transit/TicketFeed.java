package edu.transit;

public final class TicketFeed implements Subject<TransitEvent> {
    private final EventSource source;
    public TicketFeed(EventSource source) {
        this.source = source;
    }
    public void subscribe(EventType t, Observer<TransitEvent>o) {
        source.subscribe(t, o);
    }
    public void unsubscribe(EventType t, Observer<TransitEvent>o) {
        source.unsubscribe(t, o);
    }
    public void publish(TransitEvent e) {
        if (e.type() != EventType.TICKET_SOLD) throw new IllegalArgumentException("ticket events only");
        source.publish(e);
    }
}
