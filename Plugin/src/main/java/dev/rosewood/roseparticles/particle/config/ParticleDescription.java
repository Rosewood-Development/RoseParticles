package dev.rosewood.roseparticles.particle.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record ParticleDescription(String identifier,
                                  ParticleRenderParameters renderParameters) {

    public static ParticleDescription parse(JsonObject jsonObject) {
        String identifier = jsonObject.get("identifier").getAsString();
        JsonElement renderParametersElement = jsonObject.get("basic_render_parameters");
        ParticleRenderParameters renderParameters;
        if (renderParametersElement != null) {
            renderParameters = ParticleRenderParameters.parse(renderParametersElement.getAsJsonObject());
        } else {
            renderParameters = null;
        }
        return new ParticleDescription(identifier, renderParameters);
    }

}
