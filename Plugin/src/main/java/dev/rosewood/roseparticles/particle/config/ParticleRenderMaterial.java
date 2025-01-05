package dev.rosewood.roseparticles.particle.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum ParticleRenderMaterial {
    PARTICLES_ALPHA,
    PARTICLES_BLEND,
    PARTICLES_ADD,
    PARTICLES_OPAQUE,
    CUSTOM;

    public static ParticleRenderMaterial parse(JsonObject jsonObject, String property, ParticleRenderMaterial defaultValue) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return defaultValue;

        String string = element.getAsString();
        for (ParticleRenderMaterial value : values())
            if (value.name().equalsIgnoreCase(string))
                return value;

        throw new IllegalArgumentException("Unknown ParticleRenderMaterial type: " + string);
    }

}
