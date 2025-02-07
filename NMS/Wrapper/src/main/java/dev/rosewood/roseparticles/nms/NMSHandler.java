package dev.rosewood.roseparticles.nms;

import dev.rosewood.roseparticles.nms.hologram.Hologram;
import java.util.function.Consumer;
import org.bukkit.World;

public interface NMSHandler {

    /**
     * Creates a text display hologram with an initialization consumer
     *
     * @param init The consumer to call right before this hologram finishes initializing
     * @return The hologram created
     */
    Hologram createHologram(World world, Consumer<Hologram> init);

}
