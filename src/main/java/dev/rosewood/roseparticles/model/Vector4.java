package dev.rosewood.roseparticles.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public record Vector4(float a,
                      float b,
                      float c,
                      float d) {

    public static Vector4 parse(JsonElement element) {
        if (element == null || !element.isJsonArray())
            return empty();

        JsonArray jsonArray = element.getAsJsonArray();
        float a = jsonArray.get(0).getAsFloat();
        float b = jsonArray.get(1).getAsFloat();
        float c = jsonArray.get(2).getAsFloat();
        float d = jsonArray.get(3).getAsFloat();
        return new Vector4(a, b, c, d);
    }

    public static Vector4 empty() {
        return new Vector4(0, 0, 0, 0);
    }

}
