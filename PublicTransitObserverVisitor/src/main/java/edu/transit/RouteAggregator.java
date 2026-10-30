package edu.transit;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;


public final class RouteAggregator implements Observer<TransitEvent>, Subject<TransitEvent> {
    private final ConcurrentMap<String, LongAdder>arrivals = new ConcurrentHashMap<>();
    private final EventSource downstream;
    public RouteAggregator(EventSource downstream) {
        this.downstream = downstream;
    }
    public void onEvent(TransitEvent e) {
        if (e.type() == EventType.ARRIVAL) {
            arrivals.computeIfAbsent(e.routeId(), k -> new LongAdder()).increment();
            downstream.publish(TransitEvent.of(EventType.AGGREGATED, e.routeId(), e.stopId(),
                arrivals.get(e.routeId()).sum()));
        }
    }
    public long count(String route) {
        var n = arrivals.get(route);
        return n == null ? 0: n.sum();
    }
    public void subscribe(EventType t, Observer<TransitEvent>o) {
        downstream.subscribe(t, o);
    }
    public void unsubscribe(EventType t, Observer<TransitEvent>o) {
        downstream.unsubscribe(t, o);
    }
    public void publish(TransitEvent e) {
        downstream.publish(e);
    }
}
