package dev.rosewood.roseparticles.particle;

import java.util.Collection;
import java.util.List;

public interface ParticleEffect {

    /**
     * Updates this ParticleEffect.
     *
     * @param system The ParticleSystem this ParticleEffect belongs to
     */
    void update(ParticleSystem system);

    /**
     * Removes this ParticleEffect from the world, if applicable.
     */
    default void remove() {

    }

    /**
     * @return true if this particle effect is expired and needs to be removed, false otherwise
     */
    boolean expired();

    /**
     * Allows spawning additional particle effects while this particle effect is still alive.
     * Called after this particle effect is done updating.
     * By default, does nothing and returns an empty list.
     *
     * @return a collection of ParticleEffects to spawn
     */
    default Collection<ParticleEffect> createEmission() {
        return List.of();
    }

    /**
     * Allows spawning additional particle effects upon expiration.
     * Called when a particle effect is expired and being removed.
     * By default, does nothing and returns an empty list.
     *
     * @return a collection of ParticleEffects to spawn
     */
    default Collection<ParticleEffect> createExpirationEffects() {
        return List.of();
    }

}
