package dev.rosewood.roseparticles.component.particle.init;

import com.google.gson.JsonElement;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;

public record ParticleInitialSpeedComponent(MolangExpression initialSpeed,
                                            MolangExpressionVector3 initialSpeedVector) {

    public static ParticleInitialSpeedComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        MolangExpression initialSpeed = MolangParser.parse(jsonElement.getAsString());
        MolangExpressionVector3 initialSpeedVector = MolangExpressionVector3.parse(jsonElement);
        return new ParticleInitialSpeedComponent(initialSpeed, initialSpeedVector);
    }

}
