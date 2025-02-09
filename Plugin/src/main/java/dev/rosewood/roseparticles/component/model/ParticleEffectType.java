package dev.rosewood.roseparticles.component.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum ParticleEffectType {
    EMITTER,
    EMITTER_BOUND,
    PARTICLE,
    PARTICLE_WITH_VELOCITY;

    public static ParticleEffectType parse(JsonObject jsonObject, String property) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            throw new IllegalArgumentException("No particle_effect type provided");

        String string = element.getAsString();
        for (ParticleEffectType value : values())
            if (value.name().equalsIgnoreCase(string))
                return value;

        throw new IllegalArgumentException("Unknown ParticleEffectType type: " + string);
    }

}
