package dev.rosewood.roseparticles.component.emitter.lifetime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.util.ArrayList;
import java.util.List;

public record LoopingTravelDistanceEvent(float distance,
                                         List<String> effects) {

    public static List<LoopingTravelDistanceEvent> parse(JsonObject jsonObject, String property) {
        JsonElement element = jsonObject.get(property);
        if (element == null || !element.isJsonArray())
            return List.of();
        JsonArray jsonArray = element.getAsJsonArray();
        List<LoopingTravelDistanceEvent> events = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject object = jsonArray.get(i).getAsJsonObject();
            float distance = JsonHelper.parseFloat(object, "distance", 1);
            List<String> effects = JsonHelper.parseStringList(object, "effects");
            events.add(new LoopingTravelDistanceEvent(distance, effects));
        }
        return events;
    }

}
