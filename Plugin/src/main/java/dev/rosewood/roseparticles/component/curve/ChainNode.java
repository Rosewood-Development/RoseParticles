package dev.rosewood.roseparticles.component.curve;

import com.google.gson.JsonObject;

public record ChainNode(float value,
                        float slope) {

    public static ChainNode parse(JsonObject jsonObject) {
        float value = jsonObject.get("value").getAsFloat();
        float slope = jsonObject.get("slope").getAsFloat();
        return new ChainNode(value, slope);
    }

}
