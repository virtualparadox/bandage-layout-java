package eu.virtualparadox.bandage.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents an edge in the graph
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BandageEdge {
    private BandageNode source;
    private BandageNode target;
    private double weight = 1.0;

    public BandageEdge(BandageNode source, BandageNode target) {
        this.source = source;
        this.target = target;
    }
}