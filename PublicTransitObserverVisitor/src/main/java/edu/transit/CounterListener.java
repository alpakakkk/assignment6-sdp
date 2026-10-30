package edu.transit;

import java.util.concurrent.atomic.LongAdder;


public final class CounterListener implements Observer<TransitEvent> {
    private final LongAdder count = new LongAdder();
    public void onEvent(TransitEvent event) {
        count.increment();
    }
    public long count() {
        return count.sum();
    }
}
