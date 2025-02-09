package dev.rosewood.roseparticles.component.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import java.util.ArrayList;
import java.util.List;

public record SequenceEvent(List<EventDefinition> events) {

    public static SequenceEvent parse(JsonArray jsonArray) throws MolangLexException, MolangParseException {
        List<EventDefinition> events = new ArrayList<>(jsonArray.size());
        for (JsonElement element : jsonArray) {
            JsonObject eventObject = element.getAsJsonObject();
            EventDefinition eventDefinition = EventDefinition.parse(eventObject);
            events.add(eventDefinition);
        }
        return new SequenceEvent(events);
    }

    public void play(ParticleSystem particleSystem, ParticleInstance particleInstance) {
        this.events.forEach(x -> x.play(particleSystem, particleInstance));
    }

}
