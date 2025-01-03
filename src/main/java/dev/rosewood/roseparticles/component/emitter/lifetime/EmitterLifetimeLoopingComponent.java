package dev.rosewood.roseparticles.component.emitter.lifetime;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterLifetimeLoopingComponent(MolangExpression activeTime,
                                              MolangExpression sleepTime) {

    public static EmitterLifetimeLoopingComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        MolangExpression activeTime = JsonHelper.parseMolang(jsonObject, "active_time", "10");
        MolangExpression sleepTime = JsonHelper.parseMolang(jsonObject, "sleep_time", "0");
        return new EmitterLifetimeLoopingComponent(activeTime, sleepTime);
    }

}
