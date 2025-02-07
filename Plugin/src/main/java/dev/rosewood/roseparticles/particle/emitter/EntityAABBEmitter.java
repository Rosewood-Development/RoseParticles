package dev.rosewood.roseparticles.particle.emitter;

import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeEntityAABBComponent;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class EntityAABBEmitter extends BaseBoxEmitter {

    public EntityAABBEmitter(ParticleSystem particleSystem, EmitterShapeEntityAABBComponent component) {
        super(particleSystem, component.surfaceOnly(), component.direction(), component.directionCustom());
    }

    @Override
    public Vector getOffset(ParticleInstance particleInstance) {
        Entity entity = this.particleSystem.getAttachedTo();
        if (entity != null) {
            BoundingBox boundingBox = entity.getBoundingBox();
            return boundingBox.getCenter().subtract(entity.getLocation().toVector());
        } else {
            return new Vector();
        }
    }

    @Override
    public Vector getHalfDimensions(ParticleInstance particleInstance) {
        Entity entity = this.particleSystem.getAttachedTo();
        if (entity != null) {
            BoundingBox boundingBox = entity.getBoundingBox();
            return new Vector(boundingBox.getWidthX() / 2, boundingBox.getHeight() / 2, boundingBox.getWidthZ() / 2);
        } else {
            return new Vector(0.5, 0.5, 0.50);
        }
    }

}
