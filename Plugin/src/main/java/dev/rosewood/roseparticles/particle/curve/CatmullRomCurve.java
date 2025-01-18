package dev.rosewood.roseparticles.particle.curve;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import java.util.List;

public record CatmullRomCurve(MolangExpression input,
                              MolangExpression horizontalRange,
                              List<Float> nodes) implements Curve {

    public CatmullRomCurve {
        if (nodes.size() < 4) {
            throw new IllegalArgumentException("At least four nodes are required for Catmull-Rom spline");
        }
    }

    @Override
    public float evaluate() {
        float t = this.input.evaluate();
        float horizontalRangeValue = this.horizontalRange.evaluate();

        // Scale t to be within [0, 1]
        t = t / horizontalRangeValue;

        int segmentCount = this.nodes.size() - 3;
        float length = 1.0f / segmentCount;
        int segment = Math.min((int) (t / length), segmentCount - 1);
        float localT = (t - segment * length) / length;

        float p0 = this.nodes.get(segment);
        float p1 = this.nodes.get(segment + 1);
        float p2 = this.nodes.get(segment + 2);
        float p3 = this.nodes.get(segment + 3);

        return 0.5f * ((2 * p1) +
                (-p0 + p2) * localT +
                (2 * p0 - 5 * p1 + 4 * p2 - p3) * localT * localT +
                (-p0 + 3 * p1 - 3 * p2 + p3) * localT * localT * localT);
    }

    @Override
    public Curve bind(ExpressionBindingContext context, Object... values) {
        return new CatmullRomCurve(
                this.input.bind(context, values),
                this.horizontalRange.bind(context, values),
                this.nodes
        );
    }

}
