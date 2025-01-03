package dev.rosewood.roseparticles.component.emitter.rate;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterRateSteadyComponent(MolangExpression spawnRate,
                                         MolangExpression maxParticles) {

    public static EmitterRateSteadyComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        MolangExpression spawnRate = JsonHelper.parseMolang(jsonObject, "spawn_rate", "1");
        MolangExpression maxParticles = JsonHelper.parseMolang(jsonObject, "max_particles", "50");
        return new EmitterRateSteadyComponent(spawnRate, maxParticles);
    }

}
