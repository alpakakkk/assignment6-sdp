package edu.transit;

import java.util.concurrent.atomic.AtomicReference;


public final class PassengerApp implements Observer<TransitEvent> {
    private final AtomicReference<TransitEvent>latest = new AtomicReference<>();
    public void onEvent(TransitEvent e) {
        latest.set(e);
    }
    public TransitEvent latest() {
        return latest.get();
    }
}
