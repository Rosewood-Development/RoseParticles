package dev.rosewood.roseparticles.component.particle.init;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;

public record ParticleInitializationComponent(MolangExpression perRenderExpression) {

    public ParticleInitializationComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        MolangExpression perRenderExpression = JsonHelper.parseMolang(jsonObject, "per_render_expression");
        return new ParticleInitializationComponent(perRenderExpression);
    }

}
