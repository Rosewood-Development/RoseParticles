package dev.rosewood.roseparticles.particle.curve;

import dev.omega.arcane.reference.ExpressionBindingContext;

public interface Curve {

    float evaluate();

    Curve bind(ExpressionBindingContext context, Object... values);

}
