package eu.virtualparadox.bandage.model;

/**
 * Represents a node in the graph
 */
public class BandageNode {
    private String id;
    private double x;
    private double y;
    private double width;
    private double height;

    public BandageNode(String id) {
        this(id, 0.0, 0.0, 10.0, 10.0);
    }

    public BandageNode(String id, double x, double y, double w, double h) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}