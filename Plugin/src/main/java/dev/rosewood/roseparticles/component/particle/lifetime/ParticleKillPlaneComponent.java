package dev.rosewood.roseparticles.component.particle.lifetime;

import com.google.gson.JsonElement;
import dev.rosewood.roseparticles.component.model.Vector4;

public record ParticleKillPlaneComponent(Vector4 killPlane) {

    public static ParticleKillPlaneComponent parse(JsonElement jsonElement) {
        Vector4 killPlane = Vector4.parse(jsonElement);
        return new ParticleKillPlaneComponent(killPlane);
    }

}
