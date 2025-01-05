package dev.rosewood.roseparticles.particle.config;

import com.google.gson.JsonObject;

public record ParticleDescription(String identifier,
                                  ParticleRenderParameters renderParameters) {

    public static ParticleDescription parse(JsonObject jsonObject) {
        String identifier = jsonObject.get("identifier").getAsString();
        ParticleRenderParameters renderParameters = ParticleRenderParameters.parse(jsonObject.get("basic_render_parameters").getAsJsonObject());
        return new ParticleDescription(identifier, renderParameters);
    }

}
