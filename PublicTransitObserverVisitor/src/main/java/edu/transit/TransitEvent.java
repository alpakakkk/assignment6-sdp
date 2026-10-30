package edu.transit;

import java.time.Instant;
import java.util.Objects;


public record TransitEvent(EventType type, String routeId, String stopId, long value, Instant at) {
    public TransitEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(routeId);
        Objects.requireNonNull(stopId);
        Objects.requireNonNull(at);
    }
    public static TransitEvent of(EventType t, String r, String s, long v) {
        return new TransitEvent(t, r, s, v, Instant.now());
    }
}
