package edu.transit;

import java.util.concurrent.*;
import java.util.*;


public final class EventSource implements Subject<TransitEvent> {
    private final ConcurrentMap<EventType, CopyOnWriteArrayList<Observer<TransitEvent >>> registry = new ConcurrentHashMap<>();
    public void subscribe(EventType type, Observer<TransitEvent>observer) {
        registry.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).addIfAbsent(Objects.requireNonNull(observer));
    }
    public void unsubscribe(EventType type, Observer<TransitEvent>observer) {
        var list = registry.get(type);
        if (list != null) list.remove(observer);
    }
    // Snapshot iteration permits safe self-unsubscription; exception isolation keeps other observers running.
    public void publish(TransitEvent event) {
        for (var obs: registry.getOrDefault(event.type(), new CopyOnWriteArrayList<>())) {
            try {
                obs.onEvent(event);
            } catch (RuntimeException ex) {
                System.err.println("Listener failure: " + ex.getMessage());
            }
        }
    }
}
