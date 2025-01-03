package dev.rosewood.roseparticles.component.particle.init;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;

public record ParticleInitialSpeedComponent(MolangExpression initialSpeed) {

    public static ParticleInitialSpeedComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        MolangExpression initialSpeed = MolangParser.parse(jsonObject.getAsString());
        return new ParticleInitialSpeedComponent(initialSpeed);
    }

}
