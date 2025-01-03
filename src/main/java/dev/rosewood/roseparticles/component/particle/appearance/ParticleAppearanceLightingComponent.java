package dev.rosewood.roseparticles.component.particle.appearance;

import com.google.gson.JsonObject;

public record ParticleAppearanceLightingComponent() {

    public ParticleAppearanceLightingComponent parse(JsonObject jsonObject) {
        return new ParticleAppearanceLightingComponent();
    }

}
