package dev.rosewood.roseparticles.nms.v1_21_R3;

import dev.rosewood.roseparticles.nms.NMSHandler;
import dev.rosewood.roseparticles.nms.hologram.Hologram;
import dev.rosewood.roseparticles.nms.util.ReflectionUtils;
import dev.rosewood.roseparticles.nms.v1_21_R3.hologram.HologramImpl;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import org.bukkit.World;

public class NMSHandlerImpl implements NMSHandler {

    private static AtomicInteger entityCounter; // Atomic integer to generate unique entity IDs, normally private
    static {
        try {
            entityCounter = (AtomicInteger) ReflectionUtils.getFieldByPositionAndType(Entity.class, 0, AtomicInteger.class).get(null);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Hologram createHologram(World world, Consumer<Hologram> init) {
        return new HologramImpl(world, entityCounter.incrementAndGet(), init);
    }

}
