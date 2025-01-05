package dev.rosewood.roseparticles.component.particle.motion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.util.List;

public record ParticleMotionCollisionComponent(boolean enabled,
                                               float collisionDrag,
                                               float coefficientOfRestitution,
                                               float collisionRadius,
                                               boolean expireOnContact,
                                               List<ParticleMotionCollisionEvent> events) {

    public static ParticleMotionCollisionComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        boolean enabled = JsonHelper.parseBoolean(jsonObject, "enabled", true);
        float collisionDrag = JsonHelper.parseFloat(jsonObject, "collision_drag", 0);
        float coefficientOfRestitution = JsonHelper.parseFloat(jsonObject, "coefficient_of_restitution", 0);
        float collisionRadius = Math.min(0.5F, JsonHelper.parseFloat(jsonObject, "collision_radius", 0.1F));
        boolean expireOnContact = JsonHelper.parseBoolean(jsonObject, "expire_on_contact", false);
        List<ParticleMotionCollisionEvent> events = ParticleMotionCollisionEvent.parse(jsonObject, "events");
        return new ParticleMotionCollisionComponent(enabled, collisionDrag, coefficientOfRestitution, collisionRadius, expireOnContact, events);
    }

}
