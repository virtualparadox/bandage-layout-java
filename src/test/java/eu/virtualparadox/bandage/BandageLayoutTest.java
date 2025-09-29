package eu.virtualparadox.bandage;

import eu.virtualparadox.bandage.layout.BandageLayout;
import eu.virtualparadox.bandage.layout.BandageLayoutInitializer;
import eu.virtualparadox.bandage.layout.LayoutQuality;
import eu.virtualparadox.bandage.model.BandageGraph;
import eu.virtualparadox.bandage.model.BandageNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BandageLayout
 */
class BandageLayoutTest {

    @BeforeAll
    static void setUp() {
        BandageLayoutInitializer.initialize();
    }

    @Test
    @DisplayName("Basic layout test")
    void testBasicLayout() {
        final BandageGraph graph = createTestGraph();
        final BandageLayout layout = new BandageLayout();

        // Store initial positions
        double[] initialX = graph.getNodes().stream().mapToDouble(BandageNode::getX).toArray();
        double[] initialY = graph.getNodes().stream().mapToDouble(BandageNode::getY).toArray();

        // Run layout
        assertDoesNotThrow(() -> layout.layout(graph, LayoutQuality.FAST));

        // Check that positions changed
        boolean positionsChanged = false;
        for (int i = 0; i < graph.getNodes().size(); i++) {
            BandageNode node = graph.getNodes().get(i);
            if (Math.abs(node.getX() - initialX[i]) > 0.1 ||
                Math.abs(node.getY() - initialY[i]) > 0.1) {
                positionsChanged = true;
                break;
            }
        }

        assertTrue(positionsChanged, "Node positions should change after layout");
    }

    @Test
    @DisplayName("Model classes should store and update node attributes correctly")
    void testModelClasses() {
        final BandageNode node = new BandageNode("test-node", 10.0f, 20.0f, 30.0f, 40.0f);

        assertEquals("test-node", node.getId(), "Node ID should match constructor argument");
        assertEquals(10.0, node.getX(), "Node X coordinate should match constructor argument");
        assertEquals(20.0, node.getY(), "Node Y coordinate should match constructor argument");
        assertEquals(30.0, node.getWidth(), "Node width should match constructor argument");
        assertEquals(40.0, node.getHeight(), "Node height should match constructor argument");

        node.setPosition(50.0f, 60.0f);
        assertEquals(50.0, node.getX(), "Node X coordinate should be updated by setPosition()");
        assertEquals(60.0, node.getY(), "Node Y coordinate should be updated by setPosition()");
    }

    @Test
    @DisplayName("Graph creation should work with test and manual graphs")
    void testGraphCreation() {
        final BandageGraph graph = createTestGraph();
        assertEquals(5, graph.getNodeCount(), "Test graph should contain 5 nodes");
        assertEquals(5, graph.getEdgeCount(), "Test graph should contain 5 edges");

        final BandageGraph manual = new BandageGraph();
        final BandageNode n1 = new BandageNode("A");
        final BandageNode n2 = new BandageNode("B");
        manual.addNode(n1);
        manual.addNode(n2);
        manual.addEdge(n1, n2, 1.5f);

        assertEquals(2, manual.getNodeCount(), "Manual graph should contain 2 nodes");
        assertEquals(1, manual.getEdgeCount(), "Manual graph should contain 1 edge");
        assertEquals(1.5, manual.getEdges().get(0).getWeight(),
                "Edge weight should match the value specified during creation");
    }

    @Test
    @DisplayName("LayoutQuality enum should have expected values")
    void testLayoutQualityEnum() {
        final LayoutQuality[] qualities = LayoutQuality.values();
        assertEquals(5, qualities.length, "LayoutQuality enum should define exactly 5 values");
        assertEquals(0, LayoutQuality.FAST.getValue(), "FAST should map to value 0");
        assertEquals(2, LayoutQuality.MEDIUM.getValue(), "MEDIUM should map to value 2");
        assertEquals(4, LayoutQuality.MAXIMUM.getValue(), "MAXIMUM should map to value 4");
    }

    @Test
    @DisplayName("Layout should modify node positions if native library is available")
    void testLayoutFunctionality() {
        final BandageGraph graph = createTestGraph();
        final BandageLayout layout = new BandageLayout();

        final BandageNode firstNode = graph.getNodes().get(0);
        final double initialX = firstNode.getX();
        final double initialY = firstNode.getY();

        layout.layout(graph, LayoutQuality.FAST);

        final double finalX = firstNode.getX();
        final double finalY = firstNode.getY();

        final boolean changed = (Math.abs(finalX - initialX) > 0.1) ||
                (Math.abs(finalY - initialY) > 0.1);

        assertTrue(changed, "Expected node positions to change after running the layout algorithm");
    }

    /**
     * Create a simple test graph
     */
    static BandageGraph createTestGraph() {
        BandageGraph graph = new BandageGraph();

        // Create nodes
        BandageNode n1 = new BandageNode("A");
        BandageNode n2 = new BandageNode("B");
        BandageNode n3 = new BandageNode("C");
        BandageNode n4 = new BandageNode("D");
        BandageNode n5 = new BandageNode("E");

        graph.addNode(n1);
        graph.addNode(n2);
        graph.addNode(n3);
        graph.addNode(n4);
        graph.addNode(n5);

        // Create edges
        graph.addEdge(n1, n2);
        graph.addEdge(n2, n3);
        graph.addEdge(n3, n4);
        graph.addEdge(n4, n5);
        graph.addEdge(n1, n3);

        return graph;
    }

}