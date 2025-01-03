package dev.rosewood.roseparticles.component.particle.lifetime;

import com.google.gson.JsonObject;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.util.List;
import org.bukkit.Material;

public record ParticleExpireIfInBlocksComponent(List<Material> blocks) {

    public static ParticleExpireIfInBlocksComponent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        List<Material> blocks = JsonHelper.parseBlockList(jsonObject);
        return new ParticleExpireIfInBlocksComponent(blocks);
    }

}
