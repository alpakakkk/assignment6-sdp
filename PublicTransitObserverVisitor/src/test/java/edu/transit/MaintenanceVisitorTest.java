
package edu.transit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaintenanceVisitorTest {

    @Test
    void calculatesMaintenanceAcrossEntireTree() {
        assertEquals(
            107.5,
            DemoData.sample().accept(new MaintenanceVisitor()),
            0.0001
        );
    }

    @Test
    void calculatesMaintenanceOnOneStopOnly() {
        TransitNetwork network = DemoData.sample();

        TransitRoute route = (TransitRoute) network.children().get(0);
        TransitStop stop = (TransitStop) route.children().get(0);

        assertEquals(
            55.0,
            stop.accept(new MaintenanceVisitor()),
            0.0001
        );
    }
}
