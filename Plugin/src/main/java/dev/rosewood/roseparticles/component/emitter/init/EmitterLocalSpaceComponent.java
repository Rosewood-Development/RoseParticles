package dev.rosewood.roseparticles.component.emitter.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterLocalSpaceComponent(boolean position,
                                         boolean rotation,
                                         boolean velocity) {

    public static EmitterLocalSpaceComponent parse(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        boolean position = JsonHelper.parseBoolean(jsonObject, "position", false);
        boolean rotation = JsonHelper.parseBoolean(jsonObject, "rotation", false);
        boolean velocity = JsonHelper.parseBoolean(jsonObject, "velocity", false);
        return new EmitterLocalSpaceComponent(position, rotation, velocity);
    }

}
