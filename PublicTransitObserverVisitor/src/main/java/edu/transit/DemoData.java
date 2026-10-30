package edu.transit;

import java.util.*;


public final class DemoData {
    private DemoData() {
    }
    public static TransitNetwork sample() {
        return new TransitNetwork("Astana", List.of(
            new TransitRoute("R10", List.of(
                new TransitStop("S1", List.of(
                    new Bus("B1", 20, 40, true),
                    new Station("ST1", true, 100),
                    new TicketMachine("T1", 10, 120, true)
                )),
                new TransitStop("S2", List.of(
                    new Tram("TR1", 35, 80, false)
                ))
            )),
            new TransitRoute("R20", List.of(
                new TransitStop("S3", List.of(
                    new Bus("B2", 15, 30, false),
                    new TicketMachine("T2", 5, 150, false)
                ))
            ))
        ));
    }

    public static Map<String, TransitStop> indexStops(TransitNode root) {
        Map<String, TransitStop> map = new HashMap<>();
        walk(root, map);
        return Map.copyOf(map);
    }
    private static void walk(TransitNode n, Map<String, TransitStop> m) {
        if (n instanceof TransitStop s) m.put(s.id(), s);
        if (n instanceof ContainerNode c) for (var child: c.children()) walk(child, m);
    }
}
