package eu.virtualparadox.bandage.layout;

import eu.virtualparadox.bandage.model.BandageGraph;
import eu.virtualparadox.bandage.model.BandageEdge;
import eu.virtualparadox.bandage.model.BandageNode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JNI wrapper for Bandage/OGDF FMMM layout.
 */
@Slf4j
public class BandageLayout {

    static {
        loadNativeLibrary();
    }

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
            log.warn("Cannot layout an empty graph");
            return;
        }

        final int numNodes = nodes.size();
        final int numEdges = edges.size();

        log.info("Starting layout for {} nodes, {} edges", numNodes, numEdges);

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
            edgeWeights[i] = (float) edge.getWeight();
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
                log.error("Native layout returned null positions");
                return;
            }

            // Update node positions
            for (int i = 0; i < numNodes; i++) {
                final float x = positions[i * 2];
                final float y = positions[i * 2 + 1];
                nodes.get(i).setPosition(x, y);
            }

            log.info("Layout completed successfully");

        } catch (final Exception e) {
            log.error("Layout failed with exception", e);
            // Do not rethrow unless absolutely critical
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

    /**
     * Load the native library.
     * - First attempts System.loadLibrary (java.library.path).
     * - If that fails, extracts the library from resources/native and loads it.
     * - Only throws if both strategies fail.
     */
    private static void loadNativeLibrary() {
        try {
            System.loadLibrary("bandagelayout");
            log.info("Native library loaded via java.library.path");
            return;
        } catch (final UnsatisfiedLinkError e) {
            log.debug("System.loadLibrary failed: {}", e.getMessage());
        }

        // Attempt resource extraction fallback
        final String os = System.getProperty("os.name").toLowerCase();
        final String libResource;
        if (os.contains("win")) {
            libResource = "/native/bandagelayout.dll";
        } else if (os.contains("mac") || os.contains("darwin")) {
            libResource = "/native/libbandagelayout.dylib";
        } else {
            libResource = "/native/libbandagelayout.so";
        }

        try (var is = BandageLayout.class.getResourceAsStream(libResource)) {
            if (is == null) {
                throw new UnsatisfiedLinkError("Native library not found in resources: " + libResource);
            }

            final File temp = File.createTempFile("bandagelayout", libResource.substring(libResource.lastIndexOf('.')));
            temp.deleteOnExit();

            try (var osStream = new java.io.FileOutputStream(temp)) {
                is.transferTo(osStream);
            }

            System.load(temp.getAbsolutePath());
            log.info("Native library extracted and loaded from {}", temp.getAbsolutePath());

        } catch (final Exception ex) {
            throw new RuntimeException("Failed to load native library from both system path and JAR resources", ex);
        }
    }

    /**
     * Check if the native library can be loaded.
     */
    public static boolean isAvailable() {
        try {
            loadNativeLibrary();
            return true;
        } catch (final Throwable e) {
            log.debug("isAvailable check failed: {}", e.getMessage());
            return false;
        }
    }
}
