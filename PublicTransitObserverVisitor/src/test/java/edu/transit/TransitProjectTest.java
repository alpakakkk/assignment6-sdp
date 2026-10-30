package edu.transit;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;


public class TransitProjectTest {
    private final TransitNetwork network = DemoData.sample();
    @Test
    void fuelHandCalculated() {
        assertEquals(35.0, network.accept(new FuelVisitor()), 0.0001);
    }
    @Test
    void revenueHandCalculated() {
        assertEquals(1950.0, network.accept(new RevenueVisitor()), 0.0001);
    }
    @Test
    void inventoryCounts() {
        var v = new InventoryVisitor();
        network.accept(v);
        assertEquals(2, v.snapshot().get("TransitRoute").intValue());
        assertEquals(3, v.snapshot().get("TransitStop").intValue());
        assertEquals(2, v.snapshot().get("Bus").intValue());
    }
    @Test
    void inventoryAccumulatesAcrossTree() {
        var v = new InventoryVisitor();
        network.accept(v);
        assertEquals(1, v.snapshot().get("TransitNetwork").intValue());
        assertEquals(2, v.snapshot().get("TicketMachine").intValue());
    }
    @Test
    void accessibilityFilters() {
        var result = network.accept(new AccessibilityVisitor());
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(n -> n.id().equals("B1")));
    }
    @Test
    void jsonNested() {
        String json = network.accept(new JsonExportVisitor());
        assertTrue(json.contains("\"children\":[{\"type\":\"TransitRoute\""));
        assertTrue(json.contains("\"id\":\"S3\""));
    }
    @Test
    void jsonEscapesQuotes() {
        var n = new Bus("x\"q", 3, 10, true);
        assertTrue(n.accept(new JsonExportVisitor()).contains("x\\\"q"));
    }
    @Test
    void doubleDispatch() {
        assertEquals(20.0, new Bus("B", 20, 2, true).accept(new FuelVisitor()), 0.0001);
    }
    @Test
    void eventPerType() {
        var s = new EventSource();
        var a = new CounterListener();
        s.subscribe(EventType.ARRIVAL, a);
        s.publish(TransitEvent.of(EventType.DELAY, "R", "S", 1));
        assertEquals(0, a.count());
    }
    @Test
    void subscribeAndUnsubscribe() {
        var s = new EventSource();
        var c = new CounterListener();
        s.subscribe(EventType.ARRIVAL, c);
        s.publish(TransitEvent.of(EventType.ARRIVAL, "R", "S", 0));
        s.unsubscribe(EventType.ARRIVAL, c);
        s.publish(TransitEvent.of(EventType.ARRIVAL, "R", "S", 0));
        assertEquals(1, c.count());
    }
    @Test
    void selfUnsubscribeDuringNotification() {
        var s = new EventSource();
        var count = new AtomicInteger();
        @SuppressWarnings("unchecked")
        Observer<TransitEvent>[] holder = new Observer[1];
        holder[0] = e -> {
            count.incrementAndGet();
            s.unsubscribe(EventType.ARRIVAL, holder[0]);
        };
        s.subscribe(EventType.ARRIVAL, holder[0]);
        s.publish(TransitEvent.of(EventType.ARRIVAL, "R", "S", 0));
        s.publish(TransitEvent.of(EventType.ARRIVAL, "R", "S", 0));
        assertEquals(1, count.get());
    }
    @Test
    void subscribeDuringNotification() {
        var s = new EventSource();
        var added = new CounterListener();
        var once = new AtomicBoolean();
        s.subscribe(EventType.ARRIVAL, e -> {
            if (once.compareAndSet(false, true)) s.subscribe(EventType.ARRIVAL, added);
        });
        s.publish(TransitEvent.of(EventType.ARRIVAL, "R", "S", 0));
        s.publish(TransitEvent.of(EventType.ARRIVAL, "R", "S", 0));
        assertEquals(1, added.count());
    }
    @Test
    void listenerFailureDoesNotBlockOthers() {
        var s = new EventSource();
        var c = new CounterListener();
        s.subscribe(EventType.ARRIVAL, e -> {
            throw new IllegalStateException("test");
        });
        s.subscribe(EventType.ARRIVAL, c);
        s.publish(TransitEvent.of(EventType.ARRIVAL, "R", "S", 0));
        assertEquals(1, c.count());
    }
    @Test
    void wrongSourceTypeRejected() {
        var f = new TicketFeed(new EventSource());
        assertThrows(IllegalArgumentException.class, () -> f.publish(TransitEvent.of(EventType.DELAY, "R", "S", 1)));
    }
    @Test
    void aggregatorRepublishes() {
        var bus = new EventSource();
        var downstream = new EventSource();
        var a = new RouteAggregator(downstream);
        var c = new CounterListener();
        a.subscribe(EventType.AGGREGATED, c);
        bus.subscribe(EventType.ARRIVAL, a);
        bus.publish(TransitEvent.of(EventType.ARRIVAL, "R", "S", 0));
        assertEquals(1, a.count("R"));
        assertEquals(1, c.count());
    }
    @Test
    void delayThreshold() {
        var a = new DelayAlert(10);
        a.onEvent(TransitEvent.of(EventType.DELAY, "R", "S", 9));
        a.onEvent(TransitEvent.of(EventType.DELAY, "R", "S", 10));
        assertEquals(1, a.alerts());
    }
    @Test
    void ticketStats() {
        var s = new RidershipStatistics();
        s.onEvent(TransitEvent.of(EventType.TICKET_SOLD, "R", "S", 5));
        assertEquals(5, s.passengers());
    }
    @Test
    void integrationOnlyRelatedSubtree() {
        var out = new EventSource();
        var calls = new AtomicInteger();
        out.subscribe(EventType.INSPECTION_REPORT, e -> {
            assertEquals("S1", e.stopId()); assertEquals(3, e.value()); calls.incrementAndGet();
        });
        var listener = new InspectionListener(DemoData.indexStops(network), out);
        listener.onEvent(TransitEvent.of(EventType.DELAY, "R10", "S1", 12));
        listener.onEvent(TransitEvent.of(EventType.DELAY, "R10", "S2", 5));
        assertEquals(1, calls.get());
    }
    @Test
    void stressFourThreadsThousandEvents() throws Exception {
        var source = new EventSource();
        var observer = new CounterListener();
        source.subscribe(EventType.ARRIVAL, observer);
        new ProducerRunner(4).run(4, 1000, source);
        assertEquals(4000, observer.count());
    }
    @Test
    void simultaneousArrivalAggregation() throws Exception {
        var source = new EventSource();
        var downstream = new EventSource();
        var aggregator = new RouteAggregator(downstream);
        source.subscribe(EventType.ARRIVAL, aggregator);
        var downstreamCounter = new CounterListener();
        downstream.subscribe(EventType.AGGREGATED, downstreamCounter);
        new ProducerRunner(4).run(4, 1000, source);
        assertEquals(4000, aggregator.count("R10"));
        assertEquals(4000, downstreamCounter.count());
    }
    @Test
    void immutableChildLists() {
        assertThrows(UnsupportedOperationException.class, () -> network.children().add(new Bus("x", 1, 1, true)));
    }
    @Test
    void zeroFuelForTram() {
        assertEquals(0.0, new Tram("T", 20, 10, true).accept(new FuelVisitor()), 0.0001);
    }
    @Test
    void missingStopDoesNotReport() {
        var out = new EventSource();
        var c = new CounterListener();
        out.subscribe(EventType.INSPECTION_REPORT, c);
        new InspectionListener(DemoData.indexStops(network), out).onEvent(TransitEvent.of(EventType.DELAY,
            "R", "UNKNOWN", 50));
        assertEquals(0, c.count());
    }
    @Test
    void subscribersCanBeAddedIndependently() {
        var s = new EventSource();
        var c1 = new CounterListener();
        var c2 = new CounterListener();
        s.subscribe(EventType.ARRIVAL, c1);
        s.subscribe(EventType.ARRIVAL, c2);
        s.publish(TransitEvent.of(EventType.ARRIVAL, "R", "S", 1));
        assertEquals(1, c1.count());
        assertEquals(1, c2.count());
    }
}
