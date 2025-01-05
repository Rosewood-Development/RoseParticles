package dev.rosewood.roseparticles.component.particle.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.util.List;
import java.util.Map;

public record ParticleLifetimeEventsComponent(List<String> creationEvent,
                                              List<String> expirationEvent,
                                              Map<Float, List<String>> timeline) {

    public static ParticleLifetimeEventsComponent parse(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        List<String> creationEvent = JsonHelper.parseStringList(jsonObject, "creation_event");
        List<String> expirationEvent = JsonHelper.parseStringList(jsonObject, "expiration_event");
        Map<Float, List<String>> timeline = JsonHelper.parseTimeEventMap(jsonObject, "timeline");
        return new ParticleLifetimeEventsComponent(creationEvent, expirationEvent, timeline);
    }

}
