package dev.rosewood.roseparticles.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum EmitterPlaneNormalType {
    X,
    Y,
    Z,
    CUSTOM;

    public static EmitterPlaneNormalType parse(JsonObject jsonObject, String property, EmitterPlaneNormalType defaultValue) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return defaultValue;

        if (element.isJsonArray())
            return EmitterPlaneNormalType.CUSTOM;

        String type = element.getAsString();
        return switch (type.toLowerCase()) {
            case "x" -> EmitterPlaneNormalType.X;
            case "y" -> EmitterPlaneNormalType.Y;
            case "z" -> EmitterPlaneNormalType.Z;
            default -> throw new IllegalArgumentException("Unknown EmitterPlaneNormalType type: " + type);
        };
    }

}
