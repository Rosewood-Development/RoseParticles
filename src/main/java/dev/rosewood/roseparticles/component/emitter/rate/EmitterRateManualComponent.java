package dev.rosewood.roseparticles.component.emitter.rate;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterRateManualComponent(MolangExpression maxParticles) {

    public static EmitterRateManualComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        MolangExpression maxParticles = JsonHelper.parseMolang(jsonObject, "max_particles", "50");
        return new EmitterRateManualComponent(maxParticles);
    }

}
