package dev.omega.arcane.reference;

import java.util.HashMap;
import java.util.Map;

public class TempMolangVariableStorage implements MolangVariableStorage {

    private final Map<String, Float> variableStorage;

    public TempMolangVariableStorage() {
        this.variableStorage = new HashMap<>();
    }

    @Override
    public float get(String identifier) {
        return this.variableStorage.getOrDefault(identifier, 0F);
    }

    @Override
    public boolean has(String identifier) {
        return this.variableStorage.containsKey(identifier);
    }

    @Override
    public void set(String identifier, float value) {
        this.variableStorage.put(identifier, value);
    }

    @Override
    public boolean readonly() {
        return false;
    }

}
