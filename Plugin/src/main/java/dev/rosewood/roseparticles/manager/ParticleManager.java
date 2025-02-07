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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ParticleManager extends Manager implements Listener {

    private final HologramManager hologramManager;
    private final List<ParticleSystem> particleSystems;
    private final ResourceServer resourceServer;
    private final Map<String, ParticleFile> particleFiles;
    private TextureStitcher textureStitcher;
    private ScheduledTask particleTask;

    public ParticleManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.hologramManager = rosePlugin.getManager(HologramManager.class);
        this.particleSystems = new CopyOnWriteArrayList<>();
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
                if (!file.isFile())
                    continue;

                ParticleFile particleFile = ParticleFile.parse(file);
                if (particleFile != null) {
                    String name = file.getName();
                    name = name.substring(0, name.lastIndexOf('.'));
                    this.particleFiles.put(name, particleFile);
                }
            }
        }

        // Process textures
        File texturesFolder = new File(this.rosePlugin.getDataFolder(), "textures");
        this.textureStitcher = new TextureStitcher(this.rosePlugin, this.particleFiles.values(), texturesFolder, packFolder);

        // Start particle task and bind resource pack server if enabled
        if (SettingKey.RUN_ASYNC.get()) {
            this.particleTask = this.rosePlugin.getScheduler().runTaskTimerAsync(this::update, 0L, SettingKey.UPDATE_FREQUENCY.get());
        } else {
            this.particleTask = this.rosePlugin.getScheduler().runTaskTimer(this::update, 0L, SettingKey.UPDATE_FREQUENCY.get());
        }
        this.resourceServer.pack(packFolder, this.textureStitcher.getTextures());
        if (SettingKey.RESOURCE_PACK_SERVER_ENABLED.get())
            this.resourceServer.start();
    }

    public Map<String, ParticleFile> getParticleFiles() {
        return this.particleFiles;
    }

    @Override
    public void disable() {
        this.particleSystems.forEach(ParticleSystem::remove);
        this.particleSystems.clear();

        this.particleTask.cancel();
        this.particleTask = null;

        this.resourceServer.shutdown();
        this.particleFiles.clear();
    }

    public void spawnParticleSystem(Location origin, ParticleFile particleFile) {
        StitchedTexture texture = this.textureStitcher.getTexture(particleFile.description().identifier());
        this.particleSystems.add(new ParticleSystem(this.hologramManager, origin, particleFile, texture));
    }

    public void spawnParticleSystem(Entity entity, ParticleFile particleFile) {
        StitchedTexture texture = this.textureStitcher.getTexture(particleFile.description().identifier());
        this.particleSystems.add(new ParticleSystem(this.hologramManager, entity, particleFile, texture));
    }

    public void update() {
        this.particleSystems.removeIf(particleSystem -> {
            try {
                particleSystem.update();
                if (particleSystem.isFinished()) {
                    particleSystem.remove();
                    return true;
                }
                return false;
            } catch (Exception e) {
                this.rosePlugin.getLogger().warning("Particle system " + particleSystem.getIdentifier() + " threw an exception and has been forcefully removed");
                e.printStackTrace();
                particleSystem.remove();
                return true;
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.resourceServer != null)
            this.resourceServer.setResourcePack(event.getPlayer());
    }

}
