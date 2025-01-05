package dev.rosewood.roseparticles.component.particle.motion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.util.ArrayList;
import java.util.List;

public record ParticleMotionCollisionEvent(String event,
                                           float minSpeed) {

    public static List<ParticleMotionCollisionEvent> parse(JsonObject jsonObject, String property) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return List.of();

        if (element.isJsonObject()) {
            return List.of(parse(element.getAsJsonObject()));
        } else if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            List<ParticleMotionCollisionEvent> events = new ArrayList<>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++)
                events.add(parse(jsonArray.get(i).getAsJsonObject()));
            return events;
        } else {
            return List.of();
        }
    }

    private static ParticleMotionCollisionEvent parse(JsonObject jsonObject) {
        String event = jsonObject.get("event").getAsString();
        float minSpeed = Math.min(2, JsonHelper.parseFloat(jsonObject, "min_speed", 2));
        return new ParticleMotionCollisionEvent(event, minSpeed);
    }

}
