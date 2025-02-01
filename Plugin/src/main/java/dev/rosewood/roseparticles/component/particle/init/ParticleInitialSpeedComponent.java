package dev.rosewood.roseparticles.component.particle.init;

import com.google.gson.JsonElement;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;

public record ParticleInitialSpeedComponent(MolangExpressionVector3 initialSpeedVector) {

    public static ParticleInitialSpeedComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        if (jsonElement.isJsonPrimitive()) {
            MolangExpression initialSpeed = MolangParser.parse(jsonElement.getAsString());
            return new ParticleInitialSpeedComponent(new MolangExpressionVector3(initialSpeed));
        } else {
            MolangExpressionVector3 initialSpeedVector = MolangExpressionVector3.parse(jsonElement);
            return new ParticleInitialSpeedComponent(initialSpeedVector);
        }
    }

}
