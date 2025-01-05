package dev.rosewood.roseparticles.component.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record Vector2(float x,
                      float y) {

    public static Vector2 parse(JsonObject jsonObject, String property) {
        JsonElement element = jsonObject.get(property);
        if (element == null || !element.isJsonArray())
            return empty();

        JsonArray jsonArray = element.getAsJsonArray();
        float x = jsonArray.get(0).getAsFloat();
        float y = jsonArray.get(1).getAsFloat();
        return new Vector2(x, y);
    }

    public static Vector2 empty() {
        return new Vector2(0, 0);
    }

}
