package dev.rosewood.roseparticles.component.particle.motion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.util.JsonHelper;

public record ParticleMotionDynamicComponent(MolangExpressionVector3 linearAcceleration,
                                             MolangExpression linearDragCoefficient,
                                             MolangExpression rotationAcceleration,
                                             MolangExpression rotationDragCoefficient) {

    public static ParticleMotionDynamicComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MolangExpressionVector3 linearAcceleration = MolangExpressionVector3.parse(jsonObject, "linear_acceleration");
        MolangExpression linearDragCoefficient = JsonHelper.parseMolang(jsonObject, "linear_drag_coefficient");
        MolangExpression rotationAcceleration = JsonHelper.parseMolang(jsonObject, "rotation_acceleration");
        MolangExpression rotationDragCoefficient = JsonHelper.parseMolang(jsonObject, "rotation_drag_coefficient");
        return new ParticleMotionDynamicComponent(linearAcceleration, linearDragCoefficient, rotationAcceleration, rotationDragCoefficient);
    }

}
