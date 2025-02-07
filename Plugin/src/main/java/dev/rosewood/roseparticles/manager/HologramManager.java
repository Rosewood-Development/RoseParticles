package dev.rosewood.roseparticles.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import dev.rosewood.roseparticles.config.SettingKey;
import dev.rosewood.roseparticles.nms.NMSAdapter;
import dev.rosewood.roseparticles.nms.NMSHandler;
import dev.rosewood.roseparticles.nms.hologram.Hologram;
import dev.rosewood.roseparticles.nms.hologram.HologramProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class HologramManager extends Manager implements Listener {

    private final NMSHandler nmsHandler;
    private final Set<Hologram> holograms;
    private ScheduledTask watcherTask;
    private double renderDistanceSqrd;

    public HologramManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.nmsHandler = NMSAdapter.getHandler();
        this.holograms = Collections.newSetFromMap(new ConcurrentHashMap<>());

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        this.watcherTask = this.rosePlugin.getScheduler().runTaskTimerAsync(this::updateWatchers, 0L, 10L);
        this.renderDistanceSqrd = SettingKey.RENDER_DISTANCE.get();
        this.renderDistanceSqrd *= this.renderDistanceSqrd;
    }

    @Override
    public void disable() {
        this.watcherTask.cancel();
        this.watcherTask = null;

        this.holograms.forEach(Hologram::delete);
        this.holograms.clear();
    }

    private void updateWatchers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            World world = player.getWorld();
            Vector position = player.getLocation().toVector();
            for (Hologram hologram : this.holograms)
                this.updateWatcher(player, world, position, hologram);
        }
    }

    private void updateWatcher(Player player, Hologram hologram) {
        this.updateWatcher(player, player.getWorld(), player.getLocation().toVector(), hologram);
    }

    private void updateWatcher(Player player, World world, Vector position, Hologram hologram) {
        if (this.isPlayerInRange(world, position, hologram)) {
            hologram.addWatcher(player);
        } else {
            hologram.removeWatcher(player);
        }
    }

    private boolean isPlayerInRange(World world, Vector position, Hologram hologram) {
        Vector location = hologram.getProperties().get(HologramProperty.POSITION);
        return world.equals(hologram.getWorld()) && position.distanceSquared(location) <= this.renderDistanceSqrd;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.rosePlugin.getScheduler().runTaskAsync(() -> {
            Player player = event.getPlayer();
            for (Hologram hologram : this.holograms)
                this.updateWatcher(player, hologram);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.rosePlugin.getScheduler().runTaskAsync(() -> {
            Player player = event.getPlayer();
            for (Hologram hologram : this.holograms)
                hologram.removeWatcher(player);
        });
    }

    /**
     * Creates a hologram with an initialization consumer
     *
     * @param world The world the hologram belongs to
     * @param init The consumer to call right before the hologram finishes initializing
     */
    public Hologram createHologram(World world, Consumer<Hologram> init) {
        Hologram hologram = this.nmsHandler.createHologram(world, init);
        this.holograms.add(hologram);
        for (Player player : Bukkit.getOnlinePlayers())
            this.updateWatcher(player, hologram);
        return hologram;
    }

    /**
     * Deletes a hologram
     *
     * @param hologram The hologram to delete
     */
    public void deleteHologram(Hologram hologram) {
        hologram.delete();
        this.holograms.remove(hologram);
    }

}
