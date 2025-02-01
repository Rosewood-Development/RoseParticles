package dev.rosewood.roseparticles.nms.hologram;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.entity.Player;

public abstract class Hologram {

    protected final int entityId;
    protected final Set<Player> watchers;
    protected final HologramProperties properties;

    public Hologram(int entityId, Consumer<Hologram> init) {
        this.entityId = entityId;
        this.watchers = new HashSet<>();
        this.properties = new HologramProperties();

        init.accept(this);
        this.watchers.forEach(this::create);
    }

    /**
     * Adds a player to the watchers of this hologram
     *
     * @param player The player to add
     */
    public void addWatcher(Player player) {
        if (!this.watchers.contains(player)) {
            this.watchers.add(player);
            this.create(player);
            this.update(List.of(player), true);
        }
    }

    /**
     * Removes a player from the watchers of this hologram
     *
     * @param player The player to remove
     */
    public void removeWatcher(Player player) {
        if (this.watchers.remove(player))
            this.delete(player);
    }

    /**
     * @return the mutable properties of this hologram
     */
    public HologramProperties getProperties() {
        return this.properties;
    }

    /**
     * Deletes the hologram for all watchers
     */
    public void delete() {
        this.watchers.forEach(this::delete);
        this.watchers.clear();
    }

    /**
     * Creates a new hologram entity for the given player
     *
     * @param player The player to spawn the hologram for
     */
    protected abstract void create(Player player);

    /**
     * Sends the metadata packet for this hologram to the specified players if the line needs to be updated
     *
     * @param players The players to send the packet to
     * @param force true to force the packet to be sent, false otherwise
     */
    protected abstract void update(Collection<Player> players, boolean force);

    /**
     * Deletes the hologram entity for the given player
     *
     * @param player The player to delete the hologram for
     */
    protected abstract void delete(Player player);

    /**
     * Updates this hologram
     */
    public void update() {
        this.update(this.watchers, false);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Hologram hologram)) return false;
        return this.entityId == hologram.entityId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.entityId);
    }

}
