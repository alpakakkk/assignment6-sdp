package edu.transit;

import java.util.concurrent.atomic.LongAdder;


public final class DelayAlert implements Observer<TransitEvent> {
    private final long threshold;
    private final LongAdder alerts = new LongAdder();
    public DelayAlert(long threshold) {
        this.threshold = threshold;
    }
    public void onEvent(TransitEvent e) {
        if (e.type() == EventType.DELAY && e.value() >= threshold) alerts.increment();
    }
    public long alerts() {
        return alerts.sum();
    }
}
