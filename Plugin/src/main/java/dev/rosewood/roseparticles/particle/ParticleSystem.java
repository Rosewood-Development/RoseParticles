package dev.rosewood.roseparticles.particle;

import dev.rosewood.roseparticles.component.ComponentType;
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

    private final MolangSession molangSession;
    private final EmitterInstance emitter;
    private final List<ParticleInstance> particles;

    public ParticleSystem(Entity entity, ParticleFile particleFile) {
        this.attachedTo = entity;
        this.origin = null;
        this.particleFile = particleFile;
        this.molangSession = new MolangSession();
        this.emitter = new EmitterInstance(this);
        this.particles = new ArrayList<>();
    }

    public ParticleSystem(Location origin, ParticleFile particleFile) {
        this.attachedTo = null;
        this.origin = origin;
        this.particleFile = particleFile;
        this.molangSession = new MolangSession();
        this.emitter = new EmitterInstance(this);
        this.particles = new ArrayList<>();
    }

    public void update() {
//        List<ParticleEffect> newParticles = new ArrayList<>();
//        this.particles.forEach(particleEffect -> {
//            particleEffect.update(this);
//            newParticles.addAll(particleEffect.createEmission());
//        });
//
//        Iterator<ParticleEffect> particleIterator = this.particles.iterator();
//        while (particleIterator.hasNext()) {
//            ParticleEffect particleEffect = particleIterator.next();
//            if (particleEffect.expired()) {
//                particleEffect.remove();
//                newParticles.addAll(particleEffect.createExpirationEffects());
//                particleIterator.remove();
//            }
//        }
//
//        this.particles.addAll(newParticles);
    }

//    public void addParticle(ParticleEffect particleEffect) {
//        this.particles.add(particleEffect);
//    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getComponent(ComponentType<T> componentType) {
        return (T) this.particleFile.components().get(componentType);
    }

    public Location getOrigin() {
        if (this.attachedTo != null) {
            return this.attachedTo.getLocation();
        } else if (this.origin != null) {
            return this.origin.clone();
        }
        throw new IllegalStateException();
    }

    public MolangSession getMolangSession() {
        return this.molangSession;
    }

    public boolean isFinished() {
        return this.particles.isEmpty();
    }

}
