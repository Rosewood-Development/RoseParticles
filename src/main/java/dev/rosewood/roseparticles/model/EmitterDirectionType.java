package dev.rosewood.roseparticles.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum EmitterDirectionType {
    INWARDS,
    OUTWARDS,
    VELOCITY;

    public static EmitterDirectionType parse(JsonObject jsonObject, String property, EmitterDirectionType defaultValue) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return defaultValue;

        if (element.isJsonArray())
            return EmitterDirectionType.VELOCITY;

        String type = element.getAsString();
        return switch (type.toLowerCase()) {
            case "inwards" -> EmitterDirectionType.INWARDS;
            case "outwards" -> EmitterDirectionType.OUTWARDS;
            default -> throw new IllegalArgumentException("Unknown EmitterDirectionType type: " + type);
        };
    }

}
