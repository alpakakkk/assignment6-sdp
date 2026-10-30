package edu.transit;

import java.util.stream.Collectors;


public final class JsonExportVisitor extends WalkingVisitor<String> {
    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
    protected String combine(ContainerNode n) {
        return "{\"type\":\"" + n.getClass().getSimpleName() + "\",\"id\":\"" + escape(n.id()) + "\",\"children\":[" + n.children().stream().map(c -> c.accept(this)).collect(Collectors.joining(",")) + "]}";
    }
    private String leaf(TransitNode n) {
        return "{\"type\":\"" + n.getClass().getSimpleName() + "\",\"id\":\"" + escape(n.id()) + "\"}";
    }
    public String visitBus(Bus n) {
        return leaf(n);
    }
    @Override
    public String visitElectricBus(ElectricBus n) {
        return leaf(n);
    }
    public String visitTram(Tram n) {
        return leaf(n);
    }
    public String visitStation(Station n) {
        return leaf(n);
    }
    public String visitTicketMachine(TicketMachine n) {
        return leaf(n);
    }
}
