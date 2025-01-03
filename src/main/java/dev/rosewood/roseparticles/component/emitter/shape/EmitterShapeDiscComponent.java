package dev.rosewood.roseparticles.component.emitter.shape;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.model.EmitterDirectionType;
import dev.rosewood.roseparticles.model.EmitterPlaneNormalType;
import dev.rosewood.roseparticles.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterShapeDiscComponent(EmitterPlaneNormalType planeNormal,
                                        MolangExpressionVector3 planeNormalCustom,
                                        MolangExpressionVector3 offset,
                                        MolangExpression radius,
                                        boolean surfaceOnly,
                                        EmitterDirectionType direction,
                                        MolangExpressionVector3 directionCustom) {

    public static EmitterShapeDiscComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        EmitterPlaneNormalType planeNormal = EmitterPlaneNormalType.parse(jsonObject, "plane_normal", EmitterPlaneNormalType.CUSTOM);
        MolangExpressionVector3 planeNormalCustom = MolangExpressionVector3.parsePlaneNormal(jsonObject, "plane_normal");
        MolangExpressionVector3 offset = MolangExpressionVector3.parse(jsonObject, "offset");
        MolangExpression radius = JsonHelper.parseMolang(jsonObject, "radius", "1");
        boolean surfaceOnly = JsonHelper.parseBoolean(jsonObject, "surface_only", false);
        EmitterDirectionType direction = EmitterDirectionType.parse(jsonObject, "direction", EmitterDirectionType.OUTWARDS);
        MolangExpressionVector3 directionCustom = MolangExpressionVector3.parse(jsonObject, "direction");
        return new EmitterShapeDiscComponent(planeNormal, planeNormalCustom, offset, radius, surfaceOnly, direction, directionCustom);
    }

}
