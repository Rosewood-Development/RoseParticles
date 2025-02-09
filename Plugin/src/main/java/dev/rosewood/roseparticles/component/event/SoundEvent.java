package dev.rosewood.roseparticles.component.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import org.bukkit.Location;

public record SoundEvent(String soundKey,
                         float volume,
                         float pitch) {

    public static SoundEvent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        String soundKey = jsonObject.get("event_name").getAsString();
        float volume = 1.0f;
        float pitch = 1.0f;

        JsonElement volumeElement = jsonObject.get("volume"); // custom
        if (volumeElement != null)
            volume = volumeElement.getAsFloat();

        JsonElement pitchElement = jsonObject.get("pitch"); // custom
        if (pitchElement != null)
            pitch = pitchElement.getAsFloat();

        return new SoundEvent(soundKey, volume, pitch);
    }

    public void play(ParticleSystem particleSystem, ParticleInstance particleInstance) {
        Location location = particleInstance.getLocation();
        location.getWorld().playSound(location, this.soundKey, this.volume, this.pitch);
    }

}
