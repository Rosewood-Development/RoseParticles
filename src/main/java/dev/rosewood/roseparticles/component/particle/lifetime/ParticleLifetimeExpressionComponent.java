package dev.rosewood.roseparticles.component.particle.lifetime;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record ParticleLifetimeExpressionComponent(MolangExpression expirationExpression,
                                                  MolangExpression maxLifetime) {

    public ParticleLifetimeExpressionComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        MolangExpression expirationExpression = JsonHelper.parseMolang(jsonObject, "expiration_expression");
        MolangExpression maxLifetime = JsonHelper.parseMolang(jsonObject, "max_lifetime");
        return new ParticleLifetimeExpressionComponent(expirationExpression, maxLifetime);
    }

}
