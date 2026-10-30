package edu.transit;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ElectricBusTest {

    @Test
    void newElementSupportsAllVisitors() {
        ElectricBus bus = new ElectricBus("EB1", 40.0, 30, true);
        assertEquals(0.0, bus.accept(new FuelVisitor()), 0.0001);
        assertEquals(0.0, bus.accept(new RevenueVisitor()), 0.0001);
        assertEquals(10.0, bus.accept(new MaintenanceVisitor()), 0.0001);
        assertEquals(1, bus.accept(new AccessibilityVisitor()).size());
        assertTrue(bus.accept(new JsonExportVisitor()).contains("\"type\":\"ElectricBus\""));
        InventoryVisitor inventory = new InventoryVisitor();
        bus.accept(inventory);
        assertEquals(1, inventory.snapshot().get("ElectricBus"));
    }

    @Test
    void newLeafWorksInsideComposite() {
        TransitStop stop = new TransitStop("S-E", List.of(new ElectricBus("EB1", 40.0, 30, true)));
        assertEquals(10.0, stop.accept(new MaintenanceVisitor()), 0.0001);
    }
}
