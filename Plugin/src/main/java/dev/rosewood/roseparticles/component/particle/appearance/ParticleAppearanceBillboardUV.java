package dev.rosewood.roseparticles.component.particle.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.component.model.Vector2;
import dev.rosewood.roseparticles.util.JsonHelper;

public record ParticleAppearanceBillboardUV(int textureWidth,
                                            int textureHeight,
                                            Vector2 uv,
                                            Vector2 uvSize,
                                            ParticleAppearanceBillboardUVFlipbook flipbook) {

    public static ParticleAppearanceBillboardUV parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        int textureWidth = JsonHelper.parseInt(jsonObject, "texture_width", 1);
        int textureHeight = JsonHelper.parseInt(jsonObject, "texture_height", 1);
        Vector2 uv = Vector2.parse(jsonObject, "uv");
        Vector2 uvSize = Vector2.parse(jsonObject, "uv_size");
        ParticleAppearanceBillboardUVFlipbook flipbook = null;

        JsonElement flipbookElement = jsonObject.get("flipbook");
        if (flipbookElement != null && flipbookElement.isJsonObject()) {
            JsonObject flipbookObject = flipbookElement.getAsJsonObject();
            flipbook = ParticleAppearanceBillboardUVFlipbook.parse(jsonObject);
        }

        return  new ParticleAppearanceBillboardUV(textureWidth, textureHeight, uv, uvSize, flipbook);
    }

}
