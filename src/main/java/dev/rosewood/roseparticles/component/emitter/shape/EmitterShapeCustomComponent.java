package dev.rosewood.roseparticles.component.emitter.shape;

import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.model.MolangExpressionVector3;

public record EmitterShapeCustomComponent(MolangExpressionVector3 offset,
                                          MolangExpressionVector3 direction) {

    public static EmitterShapeCustomComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        MolangExpressionVector3 offset = MolangExpressionVector3.parse(jsonObject, "offset");
        MolangExpressionVector3 direction = MolangExpressionVector3.parse(jsonObject, "direction");
        return new EmitterShapeCustomComponent(offset, direction);
    }

}
