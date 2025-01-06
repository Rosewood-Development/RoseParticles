package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.config.SettingKey;
import dev.rosewood.roseparticles.datapack.StitchedTexture;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ParticleSystem {

    private final Entity attachedTo;
    private final Location origin;
    private final ParticleFile particleFile;
    private final StitchedTexture texture;

    private final ExpressionBindingContext molangContext;
    private final EmitterInstance emitter;
    private final List<ParticleInstance> particles;
    private final float deltaTime;
    private boolean doneEmitting;

    public ParticleSystem(Entity entity, ParticleFile particleFile, StitchedTexture texture) {
        this(entity, null, particleFile, texture);
    }

    public ParticleSystem(Location origin, ParticleFile particleFile, StitchedTexture texture) {
        this(null, origin, particleFile, texture);
    }

    private ParticleSystem(Entity entity, Location origin, ParticleFile particleFile, StitchedTexture texture) {
        this.attachedTo = entity;
        this.origin = origin;
        this.particleFile = particleFile;
        this.texture = texture;
        this.molangContext = ExpressionBindingContext.create();
        this.emitter = new EmitterInstance(this);
        this.particles = new ArrayList<>();
        this.deltaTime = SettingKey.UPDATE_FREQUENCY.get() / 20F;
    }

    protected EmitterInstance getEmitter() {
        return this.emitter;
    }

    protected ExpressionBindingContext getMolangContext() {
        return this.molangContext;
    }

    protected StitchedTexture getTexture() {
        return this.texture;
    }

    public void update() {
//        List<ParticleEffect> newParticles = new ArrayList<>();
        if (!this.doneEmitting) {
            this.emitter.update(this.deltaTime);
            if (this.emitter.expired())
                this.doneEmitting = true;
        }

        this.particles.removeIf(particle -> {
            particle.update(this.deltaTime);
            if (particle.expired()) {
                particle.remove();
                return true;
            } else {
                return false;
            }
        });

//        this.particles.addAll(newParticles);
    }

//    public void addParticle(ParticleEffect particleEffect) {
//        this.particles.add(particleEffect);
//    }

    @Nullable
    public <T> T getComponent(ComponentType<T> componentType) {
        return this.particleFile.getComponent(componentType);
    }

    public Location getOrigin() {
        if (this.attachedTo != null) {
            return this.attachedTo.getLocation();
        } else if (this.origin != null) {
            return this.origin.clone();
        }
        throw new IllegalStateException();
    }

    public boolean isFinished() {
        return this.doneEmitting && this.particles.isEmpty();
    }

}
