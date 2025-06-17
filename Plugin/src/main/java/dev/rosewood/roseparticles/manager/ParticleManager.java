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
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ParticleManager extends Manager implements Listener {

    private final HologramManager hologramManager;
    private final List<ParticleSystem> particleSystems;
    private final List<ParticleSystem> newParticleSystems;
    private final ResourceServer resourceServer;
    private final Map<NamespacedKey, ParticleFile> particleFiles;
    private TextureStitcher textureStitcher;
    private ScheduledTask particleTask;
    private long lastUpdateTime;
    private transient boolean running = false;

    public ParticleManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.hologramManager = rosePlugin.getManager(HologramManager.class);
        this.particleSystems = new ArrayList<>();
        this.newParticleSystems = new ArrayList<>();
        this.resourceServer = new ResourceServer(this.rosePlugin);
        this.particleFiles = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
        this.lastUpdateTime = System.currentTimeMillis();
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
                    NamespacedKey key = NamespacedKey.fromString(particleFile.description().identifier().toLowerCase(), this.rosePlugin);
                    this.particleFiles.put(key, particleFile);
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

    public Map<NamespacedKey, ParticleFile> getParticleFiles() {
        return this.particleFiles;
    }

    public ParticleFile getParticleFile(String query) {
        int colon = query.indexOf(':');
        if (colon == query.length() - 1)
            return null;

        if (colon != -1) {
            String namespace = query.substring(0, colon);
            String key = query.substring(colon + 1);
            NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
            ParticleFile particleFile = this.particleFiles.get(namespacedKey);
            if (particleFile != null)
                return particleFile;
            query = key;
        }

        for (var entry : this.particleFiles.entrySet())
            if (entry.getKey().getKey().equals(query))
                return entry.getValue();

        return null;
    }

    @Override
    public void disable() {
        this.particleSystems.forEach(ParticleSystem::remove);
        this.particleSystems.clear();

        this.newParticleSystems.forEach(ParticleSystem::remove);
        this.newParticleSystems.clear();

        this.particleTask.cancel();
        this.particleTask = null;

        this.resourceServer.shutdown();
        this.particleFiles.clear();
    }

    public ParticleSystem spawnParticleSystem(Location origin, ParticleFile particleFile) {
        StitchedTexture texture = this.textureStitcher.getTexture(particleFile.description().identifier());
        ParticleSystem particleSystem = new ParticleSystem(this.hologramManager, origin, particleFile, texture);
        this.newParticleSystems.add(particleSystem);
        return particleSystem;
    }

    public ParticleSystem spawnParticleSystem(Entity entity, ParticleFile particleFile) {
        StitchedTexture texture = this.textureStitcher.getTexture(particleFile.description().identifier());
        ParticleSystem particleSystem = new ParticleSystem(this.hologramManager, entity, particleFile, texture);
        this.newParticleSystems.add(particleSystem);
        return particleSystem;
    }

    public void update() {
        if (this.running)
            return;

        this.running = true;
        try {
            long currentTime = System.currentTimeMillis();
            float deltaTime = (currentTime - this.lastUpdateTime) / 1000.0F;
            this.lastUpdateTime = currentTime;

            this.particleSystems.addAll(this.newParticleSystems);
            this.newParticleSystems.clear();
            this.particleSystems.removeIf(particleSystem -> {
                try {
                    particleSystem.update(deltaTime);
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
        } finally {
            this.running = false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.resourceServer != null)
            this.resourceServer.setResourcePack(event.getPlayer());
    }

}
