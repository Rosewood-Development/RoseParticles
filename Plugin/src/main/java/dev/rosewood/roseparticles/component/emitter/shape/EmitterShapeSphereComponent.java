package dev.rosewood.roseparticles.component.emitter.shape;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.component.model.EmitterDirectionType;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterShapeSphereComponent(MolangExpressionVector3 offset,
                                          MolangExpression radius,
                                          boolean surfaceOnly,
                                          EmitterDirectionType direction,
                                          MolangExpressionVector3 directionCustom) {

    public static EmitterShapeSphereComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MolangExpressionVector3 offset = MolangExpressionVector3.parse(jsonObject, "offset");
        MolangExpression radius = JsonHelper.parseMolang(jsonObject, "radius", "1");
        boolean surfaceOnly = JsonHelper.parseBoolean(jsonObject, "surface_only", false);
        EmitterDirectionType direction = EmitterDirectionType.parse(jsonObject, "direction", EmitterDirectionType.OUTWARDS);
        MolangExpressionVector3 directionCustom = MolangExpressionVector3.parse(jsonObject, "direction");
        return new EmitterShapeSphereComponent(offset, radius, surfaceOnly, direction, directionCustom);
    }

}
