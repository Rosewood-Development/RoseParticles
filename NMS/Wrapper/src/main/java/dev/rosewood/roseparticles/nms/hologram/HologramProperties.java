package dev.rosewood.roseparticles.nms.hologram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.util.Vector;

public class HologramProperties {

    private final Map<HologramProperty<?>, Object> properties;
    private final Set<HologramProperty<?>> dirty;
    private Vector previousLocation, previousRotation;

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
        if (property == HologramProperty.ROTATION)
            this.previousRotation = this.get(HologramProperty.ROTATION);
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

    public Vector getPreviousRotation() {
        if (this.previousRotation == null)
            return this.get(HologramProperty.ROTATION);
        return this.previousRotation;
    }

    public void clearDirty() {
        this.dirty.clear();
    }

}
