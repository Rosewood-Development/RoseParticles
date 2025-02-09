package dev.rosewood.roseparticles.util;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class RandomCollection<T> {

    private final Map<T, Double> elementToWeight;
    private NavigableMap<Double, T> bag;

    public RandomCollection() {
        this.elementToWeight = new HashMap<>();
        this.bag = null;
    }

    public void add(double weight, T element) {
        if (weight > 0) {
            this.elementToWeight.put(element, weight);
            this.bag = null;
        }
    }

    public T next() {
        if (this.bag == null)
            this.rebuild();

        if (this.bag.isEmpty())
            return null;

        double value = ParticleUtils.RANDOM.nextDouble() * this.bag.lastKey();
        return this.bag.higherEntry(value).getValue();
    }

    private void rebuild() {
        this.bag = new TreeMap<>();
        double total = 0;
        for (Map.Entry<T, Double> entry : this.elementToWeight.entrySet()) {
            total += entry.getValue();
            this.bag.put(total, entry.getKey());
        }
    }

}
