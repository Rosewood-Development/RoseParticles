package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.reference.MolangVariableStorage;
import java.util.HashMap;
import java.util.Map;

public abstract class ParticleEffect implements MolangVariableStorage {

    protected final Map<String, Float> variables;

    public ParticleEffect() {
        this.variables = new HashMap<>();
    }

    /**
     * Updates this ParticleEffect
     */
    public abstract void update();

    @Override
    public float get(String identifier) {
        return this.variables.getOrDefault(identifier, 0F);
    }

    @Override
    public boolean has(String identifier) {
        return this.variables.containsKey(identifier);
    }

    @Override
    public void set(String identifier, float value) {
        this.variables.put(identifier, value);
    }

    @Override
    public boolean readonly() {
        return false;
    }

}
