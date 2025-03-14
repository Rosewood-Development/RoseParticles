package dev.rosewood.roseparticles.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseparticles.manager.ParticleManager;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import dev.rosewood.roseparticles.util.FireworkUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FireworkExplodeEvent;

public class FireworkListener implements Listener {

    private final RosePlugin rosePlugin;

    public FireworkListener(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFireworkExplode(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        String identifier = FireworkUtil.getFireworkIdentifier(firework.getFireworkMeta());
        if (identifier != null) {
            ParticleManager particleManager = this.rosePlugin.getManager(ParticleManager.class);
            ParticleFile particleFile = particleManager.getParticleFiles().get(NamespacedKey.fromString(identifier));
            if (particleFile == null) {
                this.rosePlugin.getLogger().warning("Firework exploded at " + firework.getLocation() + " triggered an invalid particle effect: " + identifier);
                return;
            }

            event.setCancelled(true);
            firework.remove();
            particleManager.spawnParticleSystem(firework.getLocation(), particleFile);
        }
    }

}
