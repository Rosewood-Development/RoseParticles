package dev.rosewood.roseparticles.component.emitter.rate;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterRateInstantComponent(MolangExpression numParticles) {

    public static EmitterRateInstantComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        MolangExpression numParticles = JsonHelper.parseMolang(jsonObject, "num_particles", "10");
        return new EmitterRateInstantComponent(numParticles);
    }

}
