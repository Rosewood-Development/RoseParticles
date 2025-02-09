package dev.rosewood.roseparticles.component.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import dev.rosewood.roseparticles.util.RandomCollection;

public record RandomizeEvent(RandomCollection<EventDefinition> events) {

    public static RandomizeEvent parse(JsonArray jsonArray) throws MolangLexException, MolangParseException {
        RandomCollection<EventDefinition> events = new RandomCollection<>();
        for (JsonElement element : jsonArray) {
            JsonObject eventObject = element.getAsJsonObject();
            double weight = eventObject.get("weight").getAsDouble();
            EventDefinition eventDefinition = EventDefinition.parse(eventObject);
            events.add(weight, eventDefinition);
        }
        return new RandomizeEvent(events);
    }

    public void play(ParticleSystem particleSystem, ParticleInstance particleInstance) {
        EventDefinition event = this.events.next();
        if (event != null)
            event.play(particleSystem, particleInstance);
    }

}
