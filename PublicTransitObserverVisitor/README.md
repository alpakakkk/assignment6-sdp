# TransitPulse — Assignment 6 (Public Transport, Variant 15)

Java 17 · JUnit 5 · Maven · Observer + Visitor + Composite · No runtime libraries

## Quick start

```bash
mvn clean test
mvn exec:java
```

Open the `pom.xml` in IntelliJ IDEA and select **JDK 17**. Maven will download JUnit 5 for the test phase. The demo uses deterministic synthetic data and needs no database, internet access or external services at runtime.

## Design and architecture

### Observer

Two separate live subjects: `ArrivalFeed` (ARRIVAL/DELAY) and `TicketFeed` (TICKET_SOLD). Both wrap an independent `EventSource` with a `ConcurrentHashMap<EventType,CopyOnWriteArrayList<Observer<TransitEvent>>>`. Subscribers include `CounterListener` (stop display), `PassengerApp`, `DelayAlert`, `RidershipStatistics`, `RouteAggregator` and `InspectionListener`. Aggregator is **both observer and subject** and republishes AGGREGATED updates to independent downstream subscribers. Listeners subscribe by type and may subscribe/unsubscribe during notification: snapshot iteration avoids concurrent modification; the change applies to later notifications. A handler throwing RuntimeException does not prevent other handlers from running; the exception is printed to stderr. This is an at-least-one-attempt synchronous in-process push model, not a durable message broker.

**Push choice:** immutable `TransitEvent` contains type, routeId, stopId, value and timestamp, so a subscriber needs no extra source lookup. `value` means delay minutes for DELAY, ticket quantity for TICKET_SOLD, arrival counter for AGGREGATED, accessible-device count for INSPECTION_REPORT.

### Visitor / Composite

Tree: `TransitNetwork → TransitRoute → TransitStop → Bus/Tram/Station/TicketMachine`. Three container classes, four leaf types, four edges from root to leaf (depth 4). `TransitNode.accept(v)` performs double dispatch: `new Bus(...).accept(visitor)` calls `visitor.visitBus(this)`.

**Visitors own traversal**, implemented in `WalkingVisitor`: children are recursively visited from container visits. Elements are immutable data records with only an `accept` method, so adding new operations does not modify them.

Visitors: `FuelVisitor` (numeric fuel litres), `RevenueVisitor` (numeric ticket revenue), `InventoryVisitor` (accumulated counters), `AccessibilityVisitor` (filtered element list) and `JsonExportVisitor` (nested JSON with string escaping). Inventory visitor is stateful per instance; create a new instance for each independent report. Export represents the full hierarchy, plus element type and id; numeric business fields are intentionally not part of this structural export.

**Hand-computed demo expectations:** B1 20 L + B2 15 L = **35 L**; T1 10×120 + T2 5×150 = **1950** revenue units. 2 routes, 3 stops, 2 buses, 2 ticket machines, 3 accessible leaves in S1.

### Integration

ArrivalFeed emits `DELAY` at S1 → InspectionListener indexes the matching stop → calls `AccessibilityVisitor` on **S1 only**, not the entire network → publishes `INSPECTION_REPORT` onto separate reporting subject. Threshold is 10 minutes. Unknown stops/short delays generate no reports.

### Concurrency and graceful shutdown

`ProducerRunner(int threads)` sets the ExecutorService pool size. `run(producers, perProducer, feed)` starts configurable producer tasks, awaits **every Future** (no lost tasks for successfully completed runs), then calls `shutdown()` + `awaitTermination(30 seconds)`, with `shutdownNow()` on timeout. Demo uses 4 producers × 1000 ARRIVAL events and expects exactly 4000 callbacks. `LongAdder`/`AtomicReference`/concurrent maps protect shared counters and last-event fields; no subject-wide lock. The event registry uses snapshot semantics (a subscription change does not retroactively alter in-flight dispatch). This is guaranteed for a completed normal run; forcibly interrupted or crashed processes do not provide durable delivery.

### Clean Code

`Main` is the single composition root; services receive dependencies through constructors; no lazy service initialization; domain models are immutable records; Visitor traversals and notification logic are centralized. `DemoData` supplies sample fixtures.

## Tests

`TransitProjectTest.java` has **24 JUnit 5 tests** including numeric hand calculations, nested export, double dispatch, inventory state, filtering, type subscriptions, unsubscribe/subscribe within callbacks, listener-failure isolation, aggregator chain, integration subtree, exact 4×1000 stress count, concurrent aggregation and immutability.

## Trade-off proof (must be two distinct actual Git commits)

1. **New visitor (operation extension):** add `src/main/java/edu/transit/MaintenanceVisitor.java` with methods for each existing element; optionally add its test. Do **not** change any element file. Commit `feat: add maintenance visitor without changing nodes`.
2. **New element (type extension):** introduce `ElectricBus.java`, update `TransitVisitor.java` to include `visitElectricBus`, update **all concrete visitor files** (`FuelVisitor`, `RevenueVisitor`, `InventoryVisitor`, `AccessibilityVisitor`, `JsonExportVisitor`) to support it; update demo fixture/test. Commit `feat: add electric bus node and update visitor contract`.

This is a **documented live-coding plan**, not a claim that those commits have already been created. A new visitor is cheap; a new node requires changing every visitor. Prefer Visitor when element kinds are stable and new operations frequent; avoid it when node types change constantly.

## Suggested meaningful two-week Git history

| Commit | Changes |
|---|---|
| 1 `chore: initialize Maven Java 17` | pom, gitignore |
| 2 `feat: implement typed observer subjects` | Subject, Observer, TransitEvent, sources |
| 3 `feat: add transport subscribers and aggregator` | listener classes |
| 4 `feat: model immutable transport composite` | network, route, stop, leaves |
| 5 `feat: implement visitor operations` | visitor interface and implementations |
| 6 `feat: integrate delay inspections and reports` | InspectionListener, demo |
| 7 `feat: add concurrent event producers` | ProducerRunner |
| 8 `test: cover patterns and stress behavior` | JUnit tests |
| 9 `docs: explain tradeoffs and diagram` | README, UML |
| 10 `feat: add maintenance visitor without changing nodes` | new visitor + tests (live coding) |
| 11 `feat: add electric bus node and update visitor contract` | new node + visitors + tests (live coding) |

**Important:** do not fabricate this history by pretending the project was developed over two weeks. Make actual commits for work you perform. For the two trade-off commits, you can implement the changes later as live-coding exercises following the plan above.

## UML

See [`docs/architecture.puml`](docs/architecture.puml). Paste into PlantUML or install a PlantUML IntelliJ extension to export PNG.

## Defense (short English script)

> My Assignment 6 project is TransitPulse, variant 15, public transport. I implemented two patterns: Observer for live transport events and Visitor for operations on a transport tree. ArrivalFeed and TicketFeed publish immutable typed events to subscribed listeners. A route aggregator is both a listener and a subject. The Visitor side has Network, Route, Stop and four device types. I implemented visitors for fuel, revenue, inventory, accessibility and JSON export. When a serious delay arrives, the inspection listener runs a visitor only on the affected stop and sends a new report event. My subjects use concurrent collections and snapshots, and my four-producer stress test verifies exactly 4000 events. I used constructor injection, one composition root, JUnit tests and a UML diagram.

### Typical questions

- **Why double dispatch?** `node.accept(visitor)` dynamically selects the element-specific `visitBus` or `visitTram` overload; directly calling `visit(node)` with a base static type cannot choose the correct overload.
- **Why CopyOnWriteArrayList?** Safe iteration snapshots and self-unsubscription without holding a global subject lock; writes are more expensive than reads.
- **Why use Visitor?** New calculations need a new visitor only; the cost is that every visitor must change for a new element type.
- **Why only the stop subtree?** The delay event identifies stopId; searching the index avoids unnecessary traversal of unrelated routes.
