package eu.virtualparadox.bandage.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a node in the graph
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BandageNode {
    private String id;
    private double x;
    private double y;
    private double width = 10.0;
    private double height = 10.0;

    public BandageNode(String id) {
        this.id = id;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}