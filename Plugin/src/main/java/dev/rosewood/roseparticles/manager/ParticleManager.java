package dev.rosewood.roseparticles.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import dev.rosewood.roseparticles.config.SettingKey;
import dev.rosewood.roseparticles.datapack.ResourceServer;
import dev.rosewood.roseparticles.datapack.StitchedTexture;
import dev.rosewood.roseparticles.datapack.TextureStitcher;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ParticleManager extends Manager implements Listener {

    private ScheduledTask particleTask;
    private final List<ParticleSystem> particleSystems;
    private final ResourceServer resourceServer;
    private final Map<String, ParticleFile> particleFiles;
    private TextureStitcher textureStitcher;

    public ParticleManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.particleSystems = new ArrayList<>();
        this.resourceServer = new ResourceServer(this.rosePlugin);
        this.particleFiles = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        File packFolder = new File(this.rosePlugin.getDataFolder(), "pack");
        packFolder.mkdirs();

        // Load particle files
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

        // Process textures
        File texturesFolder = new File(this.rosePlugin.getDataFolder(), "textures");
        this.textureStitcher = new TextureStitcher(this.rosePlugin, this.particleFiles.values(), texturesFolder, packFolder);

        // Start particle task and bind resource pack server if enabled
        this.particleTask = this.rosePlugin.getScheduler().runTaskTimer(this::update, 0L, SettingKey.UPDATE_FREQUENCY.get());
        this.resourceServer.pack(packFolder, this.textureStitcher.getTextures());
        if (SettingKey.RESOURCE_PACK_SERVER_ENABLED.get())
            this.resourceServer.start();
    }

    public Map<String, ParticleFile> getParticleFiles() {
        return this.particleFiles;
    }

    @Override
    public void disable() {
        this.particleTask.cancel();
        this.particleTask = null;

        this.resourceServer.shutdown();
        this.particleFiles.clear();
    }

    public void spawnParticleSystem(Location origin, ParticleFile particleFile) {
        StitchedTexture texture = this.textureStitcher.getTexture(particleFile.description().identifier());
        this.particleSystems.add(new ParticleSystem(origin, particleFile, texture));
    }

    public void spawnParticleSystem(Entity entity, ParticleFile particleFile) {
        StitchedTexture texture = this.textureStitcher.getTexture(particleFile.description().identifier());
        this.particleSystems.add(new ParticleSystem(entity, particleFile, texture));
    }

    public void update() {
        this.particleSystems.removeIf(particleSystem -> {
            particleSystem.update();
            return particleSystem.isFinished();
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.resourceServer != null)
            this.resourceServer.setResourcePack(event.getPlayer());
    }

}
