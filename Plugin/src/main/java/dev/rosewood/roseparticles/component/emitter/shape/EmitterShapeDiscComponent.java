package dev.rosewood.roseparticles.component.emitter.shape;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.ConstantExpression;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.component.model.EmitterDirectionType;
import dev.rosewood.roseparticles.component.model.EmitterPlaneNormalType;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EmitterShapeDiscComponent(EmitterPlaneNormalType planeNormal,
                                        MolangExpressionVector3 planeNormalCustom,
                                        MolangExpressionVector3 offset,
                                        MolangExpression radius,
                                        boolean surfaceOnly,
                                        EmitterDirectionType direction,
                                        MolangExpressionVector3 directionCustom) {

    public static EmitterShapeDiscComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        EmitterPlaneNormalType planeNormal = EmitterPlaneNormalType.parse(jsonObject, "plane_normal", EmitterPlaneNormalType.CUSTOM);
        MolangExpressionVector3 planeNormalCustom = switch (planeNormal) {
            case CUSTOM -> MolangExpressionVector3.parsePlaneNormal(jsonObject, "plane_normal");
            case X -> new MolangExpressionVector3(new ConstantExpression(1), new ConstantExpression(0), new ConstantExpression(0));
            case Y -> new MolangExpressionVector3(new ConstantExpression(0), new ConstantExpression(1), new ConstantExpression(0));
            case Z -> new MolangExpressionVector3(new ConstantExpression(0), new ConstantExpression(0), new ConstantExpression(1));
        };
        MolangExpressionVector3 offset = MolangExpressionVector3.parse(jsonObject, "offset");
        MolangExpression radius = JsonHelper.parseMolang(jsonObject, "radius", 1F);
        boolean surfaceOnly = JsonHelper.parseBoolean(jsonObject, "surface_only", false);
        EmitterDirectionType direction = EmitterDirectionType.parse(jsonObject, "direction", EmitterDirectionType.OUTWARDS);
        MolangExpressionVector3 directionCustom = MolangExpressionVector3.parse(jsonObject, "direction");
        return new EmitterShapeDiscComponent(planeNormal, planeNormalCustom, offset, radius, surfaceOnly, direction, directionCustom);
    }

}
