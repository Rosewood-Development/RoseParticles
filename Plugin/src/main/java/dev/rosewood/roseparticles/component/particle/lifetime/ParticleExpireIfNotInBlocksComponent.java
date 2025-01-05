package dev.rosewood.roseparticles.component.particle.lifetime;

import com.google.gson.JsonElement;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.util.List;
import org.bukkit.Material;

public record ParticleExpireIfNotInBlocksComponent(List<Material> blocks) {

    public static ParticleExpireIfNotInBlocksComponent parse(JsonElement jsonElement) {
        List<Material> blocks = JsonHelper.parseBlockList(jsonElement);
        return new ParticleExpireIfNotInBlocksComponent(blocks);
    }

}
