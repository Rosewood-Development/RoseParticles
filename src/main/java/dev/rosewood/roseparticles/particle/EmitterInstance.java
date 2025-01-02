package dev.rosewood.roseparticles.particle;

import java.util.Collection;
import java.util.function.Supplier;

public class EmitterInstance implements ParticleEffect {

    private final Supplier<Collection<ParticleEffect>> supplier;
    private final int lifetime;
    private int life;

    public EmitterInstance(int lifetime, Supplier<Collection<ParticleEffect>> supplier) {
        this.supplier = supplier;
        this.lifetime = lifetime;
    }

    @Override
    public void update(ParticleSystem system) {
        this.life++;
    }

    @Override
    public boolean expired() {
        return this.life >= this.lifetime;
    }

    @Override
    public Collection<ParticleEffect> createEmission() {
        return this.supplier.get();
    }

}
