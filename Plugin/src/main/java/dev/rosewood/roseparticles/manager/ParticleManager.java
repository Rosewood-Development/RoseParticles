package dev.rosewood.roseparticles.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import dev.rosewood.roseparticles.config.SettingKey;
import dev.rosewood.roseparticles.datapack.ResourceServer;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ParticleManager extends Manager implements Listener {

    private ScheduledTask particleTask;
    private final List<ParticleSystem> particleSystems;
    private final ResourceServer resourceServer;
    private final Map<String, ParticleFile> particleFiles;

    public ParticleManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.particleSystems = new ArrayList<>();
        this.resourceServer = new ResourceServer(this.rosePlugin);
        this.particleFiles = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        this.particleTask = this.rosePlugin.getScheduler().runTaskTimer(this::update, 0L, SettingKey.UPDATE_FREQUENCY.get());
        this.resourceServer.pack();
        if (SettingKey.RESOURCE_PACK_SERVER_ENABLED.get())
            this.resourceServer.start();

        File particlesFolder = new File(this.rosePlugin.getDataFolder(), "particles");
        particlesFolder.mkdirs();

        File[] files = particlesFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                ParticleFile particleFile = ParticleFile.parse(file);
                if (particleFile != null) {
                    String name = file.getName();
                    name = name.substring(0, name.lastIndexOf('.'));
                    this.particleFiles.put(name, particleFile);
                }
            }
        }
        this.rosePlugin.getLogger().info(this.particleFiles.toString());
    }

    public Map<String, ParticleFile> getParticleFiles() {
        return this.particleFiles;
    }

    @Override
    public void disable() {
        if (this.particleTask != null) {
            this.particleTask.cancel();
            this.particleTask = null;
        }

        this.resourceServer.shutdown();
        this.particleFiles.clear();
    }

    public void spawnParticleSystem(ParticleSystem particleSystem) {
        this.particleSystems.add(particleSystem);
    }

    public void update() {
        Iterator<ParticleSystem> particleSystemIterator = this.particleSystems.iterator();
        while (particleSystemIterator.hasNext()) {
            ParticleSystem particleSystem = particleSystemIterator.next();
            particleSystem.update();
            if (particleSystem.isFinished())
                particleSystemIterator.remove();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.resourceServer != null)
            this.resourceServer.setResourcePack(event.getPlayer());
    }

}
