package eu.virtualparadox.bandage.model;

/**
 * Represents an edge in the graph
 */
public class BandageEdge {
    private BandageNode source;
    private BandageNode target;
    private float weight;

    public BandageEdge(BandageNode source, BandageNode target) {
        this( source, target, 1.0f );
    }

    public BandageEdge(BandageNode source, BandageNode target, float weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public BandageNode getSource() {
        return source;
    }

    public BandageNode getTarget() {
        return target;
    }

    public float getWeight() {
        return weight;
    }
}