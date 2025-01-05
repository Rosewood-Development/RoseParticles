package dev.rosewood.roseparticles.component.particle.appearance;

import com.google.gson.JsonElement;

public record ParticleAppearanceLightingComponent() {

    public static ParticleAppearanceLightingComponent parse(JsonElement jsonElement) {
        return new ParticleAppearanceLightingComponent();
    }

}
