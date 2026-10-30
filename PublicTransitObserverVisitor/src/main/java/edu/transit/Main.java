package edu.transit;

public final class Main {
    public static void main(String[] args) throws InterruptedException {
        // Composition root: constructor injection, no hidden service creation in business classes.
        TransitNetwork network = DemoData.sample();
        EventSource arrivalBus = new EventSource(), ticketBus = new EventSource(), aggregateBus = new EventSource(), reportBus = new EventSource();
        ArrivalFeed arrivals = new ArrivalFeed(arrivalBus);
        TicketFeed tickets = new TicketFeed(ticketBus);
        CounterListener display = new CounterListener(), aggregateDisplay = new CounterListener(), reportDisplay = new CounterListener();
        PassengerApp passengerApp = new PassengerApp();
        DelayAlert delayAlert = new DelayAlert(10);
        RidershipStatistics ridership = new RidershipStatistics();
        RouteAggregator aggregator = new RouteAggregator(aggregateBus);
        InspectionListener inspection = new InspectionListener(DemoData.indexStops(network), reportBus);
        arrivals.subscribe(EventType.ARRIVAL, display);
        arrivals.subscribe(EventType.ARRIVAL, passengerApp);
        arrivals.subscribe(EventType.ARRIVAL, aggregator);
        arrivals.subscribe(EventType.DELAY, delayAlert);
        arrivals.subscribe(EventType.DELAY, inspection);
        tickets.subscribe(EventType.TICKET_SOLD, ridership);
        aggregator.subscribe(EventType.AGGREGATED, aggregateDisplay);
        reportBus.subscribe(EventType.INSPECTION_REPORT, reportDisplay);
        tickets.publish(TransitEvent.of(EventType.TICKET_SOLD, "R10", "S1", 3));
        arrivals.publish(TransitEvent.of(EventType.DELAY, "R10", "S1", 12));
        new ProducerRunner(4).run(4, 1000, arrivals);
        System.out.println("Arrivals=" + display.count() + ", aggregated=" + aggregateDisplay.count() + ", inspections=" + reportDisplay.count());
        System.out.println("Ridership=" + ridership.passengers() + ", alerts=" + delayAlert.alerts() + ", fuel=" + network.accept(new FuelVisitor()));
        System.out.println("Revenue=" + network.accept(new RevenueVisitor()));
        System.out.println("JSON=" + network.accept(new JsonExportVisitor()));
    }
}
