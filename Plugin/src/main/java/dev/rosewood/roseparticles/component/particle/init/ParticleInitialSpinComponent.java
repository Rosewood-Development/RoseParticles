package dev.rosewood.roseparticles.component.particle.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record ParticleInitialSpinComponent(MolangExpression rotation,
                                           MolangExpression rotationRate) {

    public static ParticleInitialSpinComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MolangExpression rotation = JsonHelper.parseMolang(jsonObject, "rotation");
        MolangExpression rotationRate = JsonHelper.parseMolang(jsonObject, "rotation_rate");
        return new ParticleInitialSpinComponent(rotation, rotationRate);
    }

}
