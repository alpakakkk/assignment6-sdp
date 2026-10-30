package edu.transit;

public final class ArrivalFeed implements Subject<TransitEvent> {
    private final EventSource source;
    public ArrivalFeed(EventSource source) {
        this.source = source;
    }
    public void subscribe(EventType t, Observer<TransitEvent>o) {
        source.subscribe(t, o);
    }
    public void unsubscribe(EventType t, Observer<TransitEvent>o) {
        source.unsubscribe(t, o);
    }
    public void publish(TransitEvent e) {
        if (e.type() != EventType.ARRIVAL && e.type() != EventType.DELAY) throw new IllegalArgumentException("arrival events only");
        source.publish(e);
    }
}
