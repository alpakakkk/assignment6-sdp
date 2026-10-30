package edu.transit;

import java.util.*;

public final class InspectionListener implements Observer<TransitEvent> {
    private final Map<String, TransitStop> stops;
    private final Subject<TransitEvent> reports;
    public InspectionListener(Map<String, TransitStop> stops, Subject<TransitEvent> reports) {
        this.stops = Map.copyOf(stops);
        this.reports = reports;
    }
    public void onEvent(TransitEvent e) {
        if (e.type() != EventType.DELAY || e.value() < 10) return;
        TransitStop stop = stops.get(e.stopId());
        if (stop == null) return;
        int accessible = stop.accept(new AccessibilityVisitor()).size();
        reports.publish(TransitEvent.of(EventType.INSPECTION_REPORT, e.routeId(), e.stopId(), accessible));
    }
}
