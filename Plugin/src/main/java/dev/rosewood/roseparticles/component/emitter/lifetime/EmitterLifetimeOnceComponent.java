package dev.rosewood.roseparticles.component.emitter.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterLifetimeOnceComponent(MolangExpression activeTime) {

    public static EmitterLifetimeOnceComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MolangExpression activeTime = JsonHelper.parseMolang(jsonObject, "active_time", "10");
        return new EmitterLifetimeOnceComponent(activeTime);
    }

}
