package eu.virtualparadox.bandage.model;

/**
 * Represents a node in the graph
 */
public class BandageNode {
    private String id;
    private float x;
    private float y;
    private float width;
    private float height;

    public BandageNode(String id) {
        this(id, 0.0f, 0.0f, 10.0f, 10.0f);
    }

    public BandageNode(String id, float x, float y, float w, float h) {
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

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}