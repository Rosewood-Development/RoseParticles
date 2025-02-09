package dev.rosewood.roseparticles.particle.curve;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import java.util.List;

public record BezierCurve(MolangExpression input,
                          MolangExpression horizontalRange,
                          List<Float> nodes) implements Curve {

    public BezierCurve {
        if (nodes.size() != 4) {
            throw new IllegalArgumentException("Exactly four nodes are required for cubic Bezier curve");
        }
    }

    @Override
    public float evaluate() {
        float t = this.input.evaluate();
        float horizontalRangeValue = this.horizontalRange.evaluate();

        // Normalize t to [0, 1] range
        t = t / horizontalRangeValue;
        t = Math.max(0, Math.min(t, 1));

        float p0 = this.nodes.get(0);
        float p1 = this.nodes.get(1);
        float p2 = this.nodes.get(2);
        float p3 = this.nodes.get(3);

        float u = 1 - t;
        float uu = u * u;
        float uuu = uu * u;
        float tt = t * t;
        float ttt = tt * t;

        return uuu * p0 +
                3 * uu * t * p1 +
                3 * u * tt * p2 +
                ttt * p3;
    }

    @Override
    public Curve bind(ExpressionBindingContext context, Object... values) {
        return new BezierCurve(
                this.input.bind(context, values),
                this.horizontalRange.bind(context, values),
                this.nodes
        );
    }

}
