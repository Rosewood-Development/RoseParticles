package dev.rosewood.roseparticles.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import dev.rosewood.roseparticles.config.SettingKey;
import dev.rosewood.roseparticles.datapack.ResourceServer;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ParticleManager extends Manager implements Listener {

    private ScheduledTask particleTask;
    private final List<ParticleSystem> particleSystems;
    private final ResourceServer resourceServer;

    public ParticleManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.particleSystems = new ArrayList<>();
        this.resourceServer = new ResourceServer(this.rosePlugin);
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        this.particleTask = this.rosePlugin.getScheduler().runTaskTimer(this::update, 0L, SettingKey.UPDATE_FREQUENCY.get());
        if (SettingKey.RESOURCE_PACK_SERVER_ENABLED.get())
            this.resourceServer.start();
    }

    @Override
    public void disable() {
        if (this.particleTask != null) {
            this.particleTask.cancel();
            this.particleTask = null;
        }

        this.resourceServer.shutdown();
    }

    public void spawnParticleSystem(ParticleSystem particleSystem) {
        this.particleSystems.add(particleSystem);
    }

    public void update() {
        Iterator<ParticleSystem> particleSystemIterator = this.particleSystems.iterator();
        while (particleSystemIterator.hasNext()) {
            ParticleSystem particleSystem = particleSystemIterator.next();
            particleSystem.update();
            if (particleSystem.getParticles().isEmpty())
                particleSystemIterator.remove();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.resourceServer != null)
            this.resourceServer.setResourcePack(event.getPlayer());
    }

}
