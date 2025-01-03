package dev.rosewood.roseparticles.component.particle.appearance;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.model.Vector2;
import dev.rosewood.roseparticles.util.JsonHelper;

public record ParticleAppearanceBillboardUVFlipbook(Vector2 baseUV,
                                                    Vector2 sizeUV,
                                                    Vector2 stepUV,
                                                    float framesPerSecond,
                                                    MolangExpression maxFrame,
                                                    boolean stretchToLifetime,
                                                    boolean loop) {

    public static ParticleAppearanceBillboardUVFlipbook parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        Vector2 baseUV = Vector2.parse(jsonObject, "base_uv");
        Vector2 sizeUV = Vector2.parse(jsonObject, "size_uv");
        Vector2 stepUV = Vector2.parse(jsonObject, "step_uv");
        float framesPerSecond = JsonHelper.parseFloat(jsonObject, "frames_per_second", -1);
        MolangExpression maxFrame = JsonHelper.parseMolang(jsonObject, "max_frame");
        boolean stretchToLifetime = JsonHelper.parseBoolean(jsonObject, "stretch_to_lifetime", false);
        boolean loop = JsonHelper.parseBoolean(jsonObject, "loop", false);
        return new ParticleAppearanceBillboardUVFlipbook(baseUV, sizeUV, stepUV, framesPerSecond, maxFrame, stretchToLifetime, loop);
    }

}
