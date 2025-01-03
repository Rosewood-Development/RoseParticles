package dev.rosewood.roseparticles.component.emitter.lifetime;

import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.util.List;
import java.util.Map;

public record EmitterLifetimeEventsComponent(List<String> creationEvent,
                                             List<String> expirationEvent,
                                             Map<Float, List<String>> timeline,
                                             Map<Float, List<String>> travelDistanceEvents,
                                             List<LoopingTravelDistanceEvent> loopingTravelDistanceEvents) {

    public static EmitterLifetimeEventsComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        List<String> creationEvent = JsonHelper.parseStringList(jsonObject, "creation_event");
        List<String> expirationEvent = JsonHelper.parseStringList(jsonObject, "expiration_event");
        Map<Float, List<String>> timeline = JsonHelper.parseTimeEventMap(jsonObject, "timeline");
        Map<Float, List<String>> travelDistanceEvents = JsonHelper.parseTimeEventMap(jsonObject, "travel_distance_events");
        List<LoopingTravelDistanceEvent> loopingTravelDistanceEvents = LoopingTravelDistanceEvent.parse(jsonObject, "looping_travel_distance_events");
        return new EmitterLifetimeEventsComponent(creationEvent, expirationEvent, timeline, travelDistanceEvents, loopingTravelDistanceEvents);
    }

}
