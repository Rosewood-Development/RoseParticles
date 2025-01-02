package dev.rosewood.roseparticles.particle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;

public class ParticleSystem {

    private final Location origin;
    private final List<ParticleEffect> particles;

    public ParticleSystem(Location origin) {
        this.origin = origin;
        this.particles = new ArrayList<>();
    }

    public void update() {
        List<ParticleEffect> newParticles = new ArrayList<>();
        this.particles.forEach(particleEffect -> {
            particleEffect.update(this);
            newParticles.addAll(particleEffect.createEmission());
        });

        Iterator<ParticleEffect> particleIterator = this.particles.iterator();
        while (particleIterator.hasNext()) {
            ParticleEffect particleEffect = particleIterator.next();
            if (particleEffect.expired()) {
                particleEffect.remove();
                newParticles.addAll(particleEffect.createExpirationEffects());
                particleIterator.remove();
            }
        }

        this.particles.addAll(newParticles);
    }

    public void addParticle(ParticleEffect particleEffect) {
        this.particles.add(particleEffect);
    }

    public Location getOrigin() {
        return this.origin.clone();
    }

    public List<ParticleEffect> getParticles() {
        return this.particles;
    }

}
