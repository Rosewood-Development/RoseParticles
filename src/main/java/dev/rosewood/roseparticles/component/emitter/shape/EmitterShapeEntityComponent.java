package dev.rosewood.roseparticles.component.emitter.shape;

import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.model.EmitterDirectionType;
import dev.rosewood.roseparticles.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterShapeEntityComponent(boolean surfaceOnly,
                                          EmitterDirectionType direction,
                                          MolangExpressionVector3 directionCustom) {

    public static EmitterShapeEntityComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        boolean surfaceOnly = JsonHelper.parseBoolean(jsonObject, "surface_only", false);
        EmitterDirectionType direction = EmitterDirectionType.parse(jsonObject, "direction", EmitterDirectionType.OUTWARDS);
        MolangExpressionVector3 directionCustom = MolangExpressionVector3.parse(jsonObject, "direction");
        return new EmitterShapeEntityComponent(surfaceOnly, direction, directionCustom);
    }

}
