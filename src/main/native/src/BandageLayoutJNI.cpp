#include <jni.h>
#include <ogdf/energybased/FMMMLayout.h>
#include <ogdf/basic/Graph.h>
#include <ogdf/basic/GraphAttributes.h>
#include <ctime>
#include <vector>
#include <stdexcept>

extern "C" {

JNIEXPORT jfloatArray JNICALL
Java_eu_virtualparadox_bandage_layout_BandageLayout_layoutGraph(
    JNIEnv* env,
    jobject obj,
    jintArray nodeIndices,
    jintArray edgeSources,
    jintArray edgeTargets,
    jfloatArray edgeWeights,
    jfloatArray nodeWidths,     // ÚJ
    jfloatArray nodeHeights,    // ÚJ
    jint numNodes,
    jint numEdges,
    jint quality,
    jboolean linearLayout,
    jdouble aspectRatio,
    jdouble componentSeparation
) {
    jint* nodes = nullptr;
    jint* sources = nullptr;
    jint* targets = nullptr;
    jfloat* weights = nullptr;
    jfloat* widths = nullptr;
    jfloat* heights = nullptr;

    try {
        // Get array data
        nodes   = env->GetIntArrayElements(nodeIndices, nullptr);
        sources = env->GetIntArrayElements(edgeSources, nullptr);
        targets = env->GetIntArrayElements(edgeTargets, nullptr);
        weights = env->GetFloatArrayElements(edgeWeights, nullptr);
        widths  = env->GetFloatArrayElements(nodeWidths, nullptr);
        heights = env->GetFloatArrayElements(nodeHeights, nullptr);

        if (!nodes || !sources || !targets || !weights || !widths || !heights) {
            throw std::runtime_error("Failed to get array elements");
        }

        // Create OGDF graph
        ogdf::Graph G;
        ogdf::GraphAttributes GA(G,
            ogdf::GraphAttributes::nodeGraphics |
            ogdf::GraphAttributes::edgeGraphics);

        // Create nodes with Java-supplied width/height
        std::vector<ogdf::node> nodeArray(numNodes);
        for (int i = 0; i < numNodes; i++) {
            nodeArray[i] = G.newNode();
            GA.width(nodeArray[i])  = widths[i]  > 0.0f ? widths[i]  : 10.0f;
            GA.height(nodeArray[i]) = heights[i] > 0.0f ? heights[i] : 10.0f;
            GA.x(nodeArray[i]) = 0.0;
            GA.y(nodeArray[i]) = 0.0;
        }

        // Create edges
        ogdf::EdgeArray<double> edgeLength(G);
        for (int i = 0; i < numEdges; i++) {
            int src = sources[i];
            int tgt = targets[i];
            if (src >= 0 && src < numNodes && tgt >= 0 && tgt < numNodes) {
                ogdf::edge e = G.newEdge(nodeArray[src], nodeArray[tgt]);
                edgeLength[e] = weights[i];
            }
        }

        // Configure FMMM like Bandage
        ogdf::FMMMLayout fmmm;
        fmmm.randSeed(static_cast<int>(std::time(nullptr)));
        fmmm.useHighLevelOptions(false);
        fmmm.unitEdgeLength(1.0);
        fmmm.allowedPositions(ogdf::FMMMOptions::AllowedPositions::All);
        fmmm.pageRatio(aspectRatio);
        fmmm.minDistCC(componentSeparation);
        fmmm.stepsForRotatingComponents(50);

        if (linearLayout) {
            fmmm.initialPlacementForces(ogdf::FMMMOptions::InitialPlacementForces::KeepPositions);
        } else {
            fmmm.initialPlacementForces(ogdf::FMMMOptions::InitialPlacementForces::RandomTime);
        }

        // Quality settings
        switch (quality) {
            case 0: fmmm.fixedIterations(3);   fmmm.fineTuningIterations(1);  fmmm.nmPrecision(2); break; // FAST
            case 1: fmmm.fixedIterations(12);  fmmm.fineTuningIterations(8);  fmmm.nmPrecision(2); break; // LOW
            case 2: fmmm.fixedIterations(30);  fmmm.fineTuningIterations(20); fmmm.nmPrecision(4); break; // MEDIUM
            case 3: fmmm.fixedIterations(60);  fmmm.fineTuningIterations(20); fmmm.nmPrecision(6); break; // HIGH
            case 4: fmmm.fixedIterations(120); fmmm.fineTuningIterations(20); fmmm.nmPrecision(8); break; // MAXIMUM
            default:fmmm.fixedIterations(30);  fmmm.fineTuningIterations(20); fmmm.nmPrecision(4);
        }

        // Run layout
        fmmm.call(GA, edgeLength);

        // Create result array
        jfloatArray result = env->NewFloatArray(numNodes * 2);
        if (!result) throw std::runtime_error("Failed to create result array");

        jfloat* positions = new jfloat[numNodes * 2];
        for (int i = 0; i < numNodes; i++) {
            positions[i * 2]     = static_cast<jfloat>(GA.x(nodeArray[i]));
            positions[i * 2 + 1] = static_cast<jfloat>(GA.y(nodeArray[i]));
        }
        env->SetFloatArrayRegion(result, 0, numNodes * 2, positions);
        delete[] positions;

        // Release arrays
        env->ReleaseIntArrayElements(nodeIndices, nodes, 0);
        env->ReleaseIntArrayElements(edgeSources, sources, 0);
        env->ReleaseIntArrayElements(edgeTargets, targets, 0);
        env->ReleaseFloatArrayElements(edgeWeights, weights, 0);
        env->ReleaseFloatArrayElements(nodeWidths, widths, 0);
        env->ReleaseFloatArrayElements(nodeHeights, heights, 0);

        return result;

    } catch (const std::exception& e) {
        if (nodes)   env->ReleaseIntArrayElements(nodeIndices, nodes, 0);
        if (sources) env->ReleaseIntArrayElements(edgeSources, sources, 0);
        if (targets) env->ReleaseIntArrayElements(edgeTargets, targets, 0);
        if (weights) env->ReleaseFloatArrayElements(edgeWeights, weights, 0);
        if (widths)  env->ReleaseFloatArrayElements(nodeWidths, widths, 0);
        if (heights) env->ReleaseFloatArrayElements(nodeHeights, heights, 0);

        jclass exceptionClass = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(exceptionClass, e.what());
        return nullptr;
    }
}

} // extern "C"
