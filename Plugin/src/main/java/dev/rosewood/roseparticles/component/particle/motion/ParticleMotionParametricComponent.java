package dev.rosewood.roseparticles.component.particle.motion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.util.JsonHelper;

public record ParticleMotionParametricComponent(MolangExpressionVector3 relativePosition,
                                                MolangExpressionVector3 direction,
                                                MolangExpression rotation) {

    public static ParticleMotionParametricComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MolangExpressionVector3 relativePosition = MolangExpressionVector3.parse(jsonObject, "relative_position");
        MolangExpressionVector3 direction = MolangExpressionVector3.parse(jsonObject, "direction");
        MolangExpression rotation = JsonHelper.parseMolang(jsonObject, "rotation");
        return new ParticleMotionParametricComponent(relativePosition, direction, rotation);
    }

}
