package dev.rosewood.roseparticles.component.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum ParticleBillboardDirectionMode {
    DERIVE_FROM_VELOCITY,
    CUSTOM_DIRECTION;

    public static ParticleBillboardDirectionMode parse(JsonObject jsonObject, String property, ParticleBillboardDirectionMode defaultValue) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return defaultValue;

        String string = element.getAsString();
        for (ParticleBillboardDirectionMode value : values())
            if (value.name().equalsIgnoreCase(string))
                return value;

        throw new IllegalArgumentException("Unknown ParticleBillboardDirectionMode type: " + string);
    }

}
