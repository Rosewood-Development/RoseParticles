package dev.rosewood.roseparticles.component.emitter.lifetime;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterLifetimeExpressionComponent(MolangExpression activationExpression,
                                                 MolangExpression expirationExpression) {

    public static EmitterLifetimeExpressionComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        MolangExpression activationExpression = JsonHelper.parseMolang(jsonObject, "activation_expression", "1");
        MolangExpression expirationExpression = JsonHelper.parseMolang(jsonObject, "expiration_expression", "0");
        return new EmitterLifetimeExpressionComponent(activationExpression, expirationExpression);
    }

}
