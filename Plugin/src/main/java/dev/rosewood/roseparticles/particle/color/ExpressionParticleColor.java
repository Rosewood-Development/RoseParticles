package dev.rosewood.roseparticles.particle.color;

import dev.rosewood.roseparticles.component.model.MolangExpressionColor;
import java.awt.Color;

public record ExpressionParticleColor(MolangExpressionColor colorExpression) implements ParticleColor {

    @Override
    public Color get() {
        return this.colorExpression.evaluate();
    }

}
