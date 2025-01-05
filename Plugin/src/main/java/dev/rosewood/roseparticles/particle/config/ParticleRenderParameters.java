package dev.rosewood.roseparticles.particle.config;

import com.google.gson.JsonObject;

public record ParticleRenderParameters(ParticleRenderMaterial renderMaterial,
                                       String renderMaterialCustom,
                                       String texturePath) {

    public static ParticleRenderParameters parse(JsonObject jsonObject) {
        ParticleRenderMaterial renderMaterial = ParticleRenderMaterial.parse(jsonObject, "material", ParticleRenderMaterial.PARTICLES_ALPHA);
        String renderMaterialCustom = jsonObject.get("material").getAsString();
        String texturePath = jsonObject.get("texture").getAsString();
        return new ParticleRenderParameters(renderMaterial, renderMaterialCustom, texturePath);
    }

}
