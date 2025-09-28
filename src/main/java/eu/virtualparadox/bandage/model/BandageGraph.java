package eu.virtualparadox.bandage.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple graph data structure
 */
@Data
@Slf4j
public class BandageGraph {
    private List<BandageNode> nodes = new ArrayList<>();
    private List<BandageEdge> edges = new ArrayList<>();

    public void addNode(BandageNode node) {
        nodes.add(node);
    }

    public void addEdge(BandageEdge edge) {
        edges.add(edge);
    }

    public void addEdge(BandageNode source, BandageNode target) {
        addEdge(new BandageEdge(source, target));
    }

    public void addEdge(BandageNode source, BandageNode target, double weight) {
        addEdge(new BandageEdge(source, target, weight));
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }
}