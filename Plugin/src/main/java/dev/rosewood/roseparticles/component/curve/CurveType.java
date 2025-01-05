package dev.rosewood.roseparticles.component.curve;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum CurveType {
    LINEAR,
    BEZIER,
    BEZIER_CHAIN,
    CATMULL_ROM;

    public static CurveType parse(JsonObject jsonObject, String property, CurveType defaultValue) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return defaultValue;

        String string = element.getAsString();
        for (CurveType value : values())
            if (value.name().equalsIgnoreCase(string))
                return value;

        throw new IllegalArgumentException("Unknown CurveType type: " + string);
    }

}
