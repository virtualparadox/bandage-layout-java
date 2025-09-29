package eu.virtualparadox.bandage.layout;

import eu.virtualparadox.bandage.model.BandageGraph;
import eu.virtualparadox.bandage.model.BandageEdge;
import eu.virtualparadox.bandage.model.BandageNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JNI wrapper for Bandage/OGDF FMMM layout.
 */
public class BandageLayout {

    // Native method declaration
    private native float[] layoutGraph(
            int[] nodeIndices,
            int[] edgeSources,
            int[] edgeTargets,
            float[] edgeWeights,
            int numNodes,
            int numEdges,
            int quality,
            boolean linearLayout,
            double aspectRatio,
            double componentSeparation
    );

    /**
     * Layout the graph using Bandage's FMMM algorithm.
     */
    public void layout(final BandageGraph graph,
                       final LayoutQuality quality,
                       final boolean linearLayout,
                       final double aspectRatio,
                       final double componentSeparation) {

        final List<BandageNode> nodes = graph.getNodes();
        final List<BandageEdge> edges = graph.getEdges();

        if (nodes.isEmpty()) {
            return;
        }

        final int numNodes = nodes.size();
        final int numEdges = edges.size();

        // Create node index mapping
        final Map<BandageNode, Integer> nodeIndexMap = new HashMap<>();
        final int[] nodeIndices = new int[numNodes];
        for (int i = 0; i < numNodes; i++) {
            final BandageNode node = nodes.get(i);
            nodeIndexMap.put(node, i);
            nodeIndices[i] = i;
        }

        // Prepare edge data
        final int[] edgeSources = new int[numEdges];
        final int[] edgeTargets = new int[numEdges];
        final float[] edgeWeights = new float[numEdges];

        for (int i = 0; i < numEdges; i++) {
            final BandageEdge edge = edges.get(i);
            edgeSources[i] = nodeIndexMap.get(edge.getSource());
            edgeTargets[i] = nodeIndexMap.get(edge.getTarget());
            edgeWeights[i] = edge.getWeight();
        }

        try {
            // Call native OGDF layout
            final float[] positions = layoutGraph(
                    nodeIndices,
                    edgeSources,
                    edgeTargets,
                    edgeWeights,
                    numNodes,
                    numEdges,
                    quality.getValue(),
                    linearLayout,
                    aspectRatio,
                    componentSeparation
            );

            if (positions == null) {
                return;
            }

            // Update node positions
            for (int i = 0; i < numNodes; i++) {
                final float x = positions[i * 2];
                final float y = positions[i * 2 + 1];
                nodes.get(i).setPosition(x, y);
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Can't layout graph", e);
        }
    }

    /**
     * Layout with Bandage defaults.
     */
    public void layout(final BandageGraph graph, final LayoutQuality quality) {
        layout(graph, quality, false, 1.0, 100.0);
    }

    /**
     * Layout for linear graphs.
     */
    public void layoutLinear(final BandageGraph graph, final LayoutQuality quality) {
        layout(graph, quality, true, 1.0, 100.0);
    }
}
