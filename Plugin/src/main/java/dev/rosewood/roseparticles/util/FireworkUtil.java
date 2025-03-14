package dev.rosewood.roseparticles.util;

import dev.rosewood.roseparticles.RoseParticles;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public final class FireworkUtil {

    public static final NamespacedKey FIREWORK_KEY = new NamespacedKey(RoseParticles.getInstance(), "firework_particle_id");

    private FireworkUtil() {

    }

    public static ItemStack createFireworkItem(ParticleFile particleFile, int power) {
        ItemStack rocketItem = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) rocketItem.getItemMeta();
        meta.setPower(power);
        meta.getPersistentDataContainer().set(FIREWORK_KEY, PersistentDataType.STRING, particleFile.description().identifier());
        rocketItem.setItemMeta(meta);
        return rocketItem;
    }

    public static void spawnFirework(String identifier, Location location, int power) {
        location.getWorld().spawn(location, Firework.class, firework -> {
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(power);
            firework.setFireworkMeta(meta);
            firework.getPersistentDataContainer().set(FIREWORK_KEY, PersistentDataType.STRING, identifier);
        });
    }

    public static String getFireworkIdentifier(PersistentDataHolder holder) {
        return holder.getPersistentDataContainer().get(FIREWORK_KEY, PersistentDataType.STRING);
    }

}
