package dev.rosewood.roseparticles.component.particle.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record ParticleLifetimeExpressionComponent(MolangExpression expirationExpression,
                                                  MolangExpression maxLifetime) {

    public static ParticleLifetimeExpressionComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MolangExpression expirationExpression = JsonHelper.parseMolang(jsonObject, "expiration_expression");
        MolangExpression maxLifetime = JsonHelper.parseMolang(jsonObject, "max_lifetime", null);
        return new ParticleLifetimeExpressionComponent(expirationExpression, maxLifetime);
    }

}
