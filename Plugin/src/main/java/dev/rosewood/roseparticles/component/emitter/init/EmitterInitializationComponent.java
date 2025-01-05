package dev.rosewood.roseparticles.component.emitter.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterInitializationComponent(MolangExpression creationExpression,
                                             MolangExpression perUpdateExpression) {

    public static EmitterInitializationComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MolangExpression creationExpression = JsonHelper.parseMolang(jsonObject, "creation_expression");
        MolangExpression perUpdateExpression = JsonHelper.parseMolang(jsonObject, "per_update_expression");
        return new EmitterInitializationComponent(creationExpression, perUpdateExpression);
    }

}
