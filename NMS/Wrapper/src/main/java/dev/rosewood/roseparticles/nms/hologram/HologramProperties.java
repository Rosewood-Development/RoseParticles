package dev.rosewood.roseparticles.nms.hologram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.util.Vector;

public class HologramProperties {

    private final Map<HologramProperty<?>, Object> properties;
    private final Set<HologramProperty<?>> dirty;
    private Vector previousLocation;

    public HologramProperties() {
        this.properties = new HashMap<>();
        this.dirty = new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(HologramProperty<T> property) {
        return (T) this.properties.get(property);
    }

    public <T> void set(HologramProperty<T> property, T value) {
        if (property == HologramProperty.POSITION)
            this.previousLocation = this.get(HologramProperty.POSITION);
        this.properties.put(property, value);
        this.dirty.add(property);
    }

    public Set<HologramProperty<?>> getAvailable() {
        return this.properties.keySet();
    }

    public Set<HologramProperty<?>> getDirty() {
        return this.dirty;
    }

    public Vector getPreviousPosition() {
        if (this.previousLocation == null)
            return this.get(HologramProperty.POSITION);
        return this.previousLocation;
    }

    public void clearDirty() {
        this.dirty.clear();
    }

}
