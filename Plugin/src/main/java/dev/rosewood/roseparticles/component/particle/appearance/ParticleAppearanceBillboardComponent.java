package dev.rosewood.roseparticles.component.particle.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector2;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.component.model.ParticleBillboardDirectionMode;
import dev.rosewood.roseparticles.component.model.ParticleBillboardFaceCameraMode;
import dev.rosewood.roseparticles.util.JsonHelper;

public record ParticleAppearanceBillboardComponent(MolangExpressionVector2 size,
                                                   ParticleBillboardFaceCameraMode faceCameraMode,
                                                   ParticleBillboardDirectionMode direction,
                                                   float directionMinSpeedThreshold,
                                                   MolangExpressionVector3 directionCustomDirection) {

    public static ParticleAppearanceBillboardComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MolangExpressionVector2 size = MolangExpressionVector2.parse(jsonObject, "size");
        ParticleBillboardFaceCameraMode faceCameraMode = ParticleBillboardFaceCameraMode.parse(jsonObject, "face_camera_mode", ParticleBillboardFaceCameraMode.ROTATE_XYZ);

        ParticleBillboardDirectionMode direction;
        float directionMinSpeedThreshold;
        MolangExpressionVector3 directionCustomDirection;

        JsonElement directionElement = jsonObject.get("direction");
        if (directionElement == null) {
            direction = ParticleBillboardDirectionMode.DERIVE_FROM_VELOCITY;
            directionMinSpeedThreshold = 0.01F;
            directionCustomDirection = MolangExpressionVector3.empty();
        } else {
            JsonObject directionObject = directionElement.getAsJsonObject();
            direction = ParticleBillboardDirectionMode.parse(directionObject, "mode", ParticleBillboardDirectionMode.DERIVE_FROM_VELOCITY);
            if (direction == ParticleBillboardDirectionMode.DERIVE_FROM_VELOCITY) {
                directionMinSpeedThreshold = JsonHelper.parseFloat(directionObject, "min_speed_threshold", 0.01F);
                directionCustomDirection = MolangExpressionVector3.empty();
            } else {
                directionMinSpeedThreshold = 0.01F;
                directionCustomDirection = MolangExpressionVector3.parse(directionObject, "custom_direction");
            }
        }

        return new ParticleAppearanceBillboardComponent(size, faceCameraMode, direction, directionMinSpeedThreshold, directionCustomDirection);
    }

}
