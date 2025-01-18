package dev.rosewood.roseparticles.particle.color;

import dev.omega.arcane.ast.MolangExpression;
import java.awt.Color;
import java.util.NavigableMap;

public record GradientParticleColor(MolangExpression interpolantExpression,
                                    NavigableMap<Float, Color> gradient) implements ParticleColor {

    public GradientParticleColor {
        if (gradient.size() < 2) {
            throw new IllegalArgumentException("Gradient must have at least 2 nodes");
        }
    }

    @Override
    public Color get() {
        float t = this.interpolantExpression.evaluate();
        // Clamp t to be within [0, 1]
        t = Math.max(0, Math.min(t, 1));

        var lowerEntry = this.gradient.floorEntry(t);
        var higherEntry = this.gradient.ceilingEntry(t);

        if (lowerEntry == null || higherEntry == null) {
            // Edge cases where t is exactly 0 or 1
            return (lowerEntry == null) ? higherEntry.getValue() : lowerEntry.getValue();
        }

        // Edge case where t is exactly along a gradient border
        if (lowerEntry.equals(higherEntry))
            return lowerEntry.getValue();

        float lowerKey = lowerEntry.getKey();
        float higherKey = higherEntry.getKey();
        Color lowerColor = lowerEntry.getValue();
        Color higherColor = higherEntry.getValue();

        // Calculate interpolation factor within the segment
        float segmentT = (t - lowerKey) / (higherKey - lowerKey);

        // Interpolate between lowerColor and higherColor
        int r = (int) (lowerColor.getRed() * (1 - segmentT) + higherColor.getRed() * segmentT);
        int g = (int) (lowerColor.getGreen() * (1 - segmentT) + higherColor.getGreen() * segmentT);
        int b = (int) (lowerColor.getBlue() * (1 - segmentT) + higherColor.getBlue() * segmentT);
        int a = (int) (lowerColor.getAlpha() * (1 - segmentT) + higherColor.getAlpha() * segmentT);

        return new Color(r, g, b, a);
    }
}

