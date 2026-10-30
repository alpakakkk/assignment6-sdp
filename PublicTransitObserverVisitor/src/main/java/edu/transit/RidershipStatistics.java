package edu.transit;

import java.util.concurrent.atomic.LongAdder;


public final class RidershipStatistics implements Observer<TransitEvent> {
    private final LongAdder passengers = new LongAdder();
    public void onEvent(TransitEvent e) {
        if (e.type() == EventType.TICKET_SOLD) passengers.add(e.value());
    }
    public long passengers() {
        return passengers.sum();
    }
}
