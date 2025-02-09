package dev.rosewood.roseparticles.particle.curve;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.curve.ChainNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

public record BezierChainCurve(MolangExpression input,
                               MolangExpression horizontalRange,
                               SortedMap<Float, ChainNode> nodes) implements Curve {

    public BezierChainCurve {
        if (nodes.size() < 2) {
            throw new IllegalArgumentException("At least two nodes are required for Bezier chain curve");
        }
    }

    @Override
    public float evaluate() {
        float t = this.input.evaluate();
        float horizontalRangeValue = this.horizontalRange.evaluate();

        // Normalize t to [0, 1] range based on horizontal range
        float normalizedT = t / horizontalRangeValue;
        normalizedT = Math.max(0, Math.min(normalizedT, 1));

        List<Float> keys = new ArrayList<>(this.nodes.keySet());
        int index = Collections.binarySearch(keys, normalizedT);

        if (index >= 0) {
            // Exact match found
            if (index == keys.size() - 1) {
                return this.nodes.get(keys.get(index)).value();
            }
        } else {
            // Find insertion point
            index = -index - 1;
            if (index == 0) {
                return this.nodes.get(keys.getFirst()).value();
            } else if (index >= keys.size()) {
                return this.nodes.get(keys.getLast()).value();
            }
            index--; // Move to the previous key
        }

        // Get current segment boundaries
        Float startKey = keys.get(index);
        Float endKey = keys.get(index + 1);
        ChainNode startNode = this.nodes.get(startKey);
        ChainNode endNode = this.nodes.get(endKey);

        // Calculate segment parameters
        float segmentDuration = endKey - startKey;
        float localT = (normalizedT - startKey) / segmentDuration;

        // Calculate cubic Bezier control points using slopes
        float p0 = startNode.value();
        float p1 = p0 + (startNode.slope() * segmentDuration) / 3;
        float p3 = endNode.value();
        float p2 = p3 - (endNode.slope() * segmentDuration) / 3;

        // Compute cubic Bezier interpolation
        float omt = 1 - localT;
        float omt2 = omt * omt;
        float omt3 = omt2 * omt;
        float t2 = localT * localT;
        float t3 = t2 * localT;

        return omt3 * p0 +
                3 * omt2 * localT * p1 +
                3 * omt * t2 * p2 +
                t3 * p3;
    }

    @Override
    public Curve bind(ExpressionBindingContext context, Object... values) {
        return new BezierChainCurve(
                this.input.bind(context, values),
                this.horizontalRange.bind(context, values),
                this.nodes
        );
    }

}
