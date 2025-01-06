package dev.omega.arcane.util;

import dev.omega.arcane.reference.MolangVariableStorage;
import java.util.HashMap;
import java.util.Map;

public class DummyStorageEntityObject implements MolangVariableStorage {

    private final Map<String, Float> variableStorage;

    public DummyStorageEntityObject() {
        this.variableStorage = new HashMap<>();
        this.variableStorage.put("age", 37F);
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
