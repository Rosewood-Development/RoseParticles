package dev.rosewood.roseparticles.component.particle.lifetime;

import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.model.Vector4;

public record ParticleLifetimeKillPlaneComponent(Vector4 killPlane) {

    public static ParticleLifetimeKillPlaneComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        Vector4 killPlane = Vector4.parse(jsonObject);
        return new ParticleLifetimeKillPlaneComponent(killPlane);
    }

}
