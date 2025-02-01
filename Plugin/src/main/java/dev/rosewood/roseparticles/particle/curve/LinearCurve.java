package dev.rosewood.roseparticles.particle.curve;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import java.util.List;

public record LinearCurve(MolangExpression input,
                          MolangExpression horizontalRange,
                          List<Float> nodes) implements Curve {

    public LinearCurve {
        if (nodes.size() < 2) {
            throw new IllegalArgumentException("At least two nodes are required for linear curve");
        }
    }

    @Override
    public float evaluate() {
        float t = this.input.evaluate();
        float horizontalRangeValue = this.horizontalRange.evaluate();

        // Scale t to be within [0, 1]
        t = t / horizontalRangeValue;

        t = Math.max(0, Math.min(t, 1));

        // Find the two nodes that t is between
        int index = (int) (t * (this.nodes.size() - 1));
        float p0 = this.nodes.get(index);
        float p1 = this.nodes.get(Math.min(index + 1, this.nodes.size() - 1));
        return p0 + (p1 - p0) * (t * (this.nodes.size() - 1) - index);
    }

    @Override
    public Curve bind(ExpressionBindingContext context, Object... values) {
        return new LinearCurve(
                this.input.bind(context, values),
                this.horizontalRange.bind(context, values),
                this.nodes
        );
    }

}
