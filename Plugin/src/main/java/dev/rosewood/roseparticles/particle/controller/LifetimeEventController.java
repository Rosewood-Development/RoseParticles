package dev.rosewood.roseparticles.particle.controller;

import dev.rosewood.roseparticles.component.emitter.lifetime.EmitterLifetimeEventsComponent;
import dev.rosewood.roseparticles.component.emitter.lifetime.LoopingTravelDistanceEvent;
import dev.rosewood.roseparticles.component.particle.lifetime.ParticleLifetimeEventsComponent;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;

public class LifetimeEventController {

    private final ParticleSystem particleSystem;
    private final ParticleInstance particleInstance;
    private final List<String> creationEvents;
    private final List<String> expirationEvents;
    private final Map<Float, List<String>> timeline;
    private final Map<Float, List<String>> travelDistanceEvents;
    private final List<LoopingTravelDistanceEvent> loopingTravelDistanceEvents;

    private Location previousLocation;
    private float previousAge;
    private float accumulatedTravelDistance;
    private final Set<Float> triggeredTravelDistanceThresholds;
    private final Map<LoopingTravelDistanceEvent, Float> loopingProgress;

    public LifetimeEventController(ParticleSystem particleSystem, EmitterLifetimeEventsComponent component) {
        this.particleSystem = particleSystem;
        this.particleInstance = null;

        this.creationEvents = component.creationEvent();
        this.expirationEvents = component.expirationEvent();
        this.timeline = component.timeline();
        this.travelDistanceEvents = component.travelDistanceEvents();
        this.loopingTravelDistanceEvents = component.loopingTravelDistanceEvents();
        this.previousAge = 0.0f;
        this.accumulatedTravelDistance = 0.0f;
        this.triggeredTravelDistanceThresholds = new HashSet<>();
        this.loopingProgress = new HashMap<>();
    }

    public LifetimeEventController(ParticleSystem particleSystem, ParticleInstance particleInstance, ParticleLifetimeEventsComponent component) {
        this.particleSystem = particleSystem;
        this.particleInstance = particleInstance;

        this.creationEvents = component.creationEvent();
        this.expirationEvents = component.expirationEvent();
        this.timeline = component.timeline();
        this.travelDistanceEvents = null;
        this.loopingTravelDistanceEvents = null;
        this.previousAge = 0.0f;
        this.accumulatedTravelDistance = 0.0f;
        this.triggeredTravelDistanceThresholds = new HashSet<>();
        this.loopingProgress = new HashMap<>();
    }

    public void onCreation() {
        for (String event : this.creationEvents)
            this.particleSystem.playEvent(event, this.particleInstance);
    }

    public void onExpiration() {
        for (String event : this.expirationEvents)
            this.particleSystem.playEvent(event, this.particleInstance);
    }

    public void update() {
        Location location;
        float age;
        if (this.particleInstance != null) {
            location = this.particleInstance.getLocation();
            age = this.particleInstance.get("age");
        } else {
            location = this.particleSystem.getOrigin();
            age = this.particleSystem.getEmitter().get("age");
        }

        if (age < this.previousAge) {
            this.previousAge = 0.0f;
            this.accumulatedTravelDistance = 0.0f;
            this.triggeredTravelDistanceThresholds.clear();
            this.loopingProgress.clear();
        }

        // Handle timeline events
        if (this.timeline != null) {
            for (var entry : this.timeline.entrySet()) {
                if (this.previousAge < entry.getKey() && age >= entry.getKey()) {
                    if (this.particleInstance != null) {
                        this.particleInstance.set("age", entry.getKey());
                        for (String event : entry.getValue())
                            this.particleSystem.playEvent(event, this.particleInstance);
                        this.particleInstance.set("age", age);
                    } else {
                        for (String event : entry.getValue())
                            this.particleSystem.playEvent(event, null);
                    }
                }
            }
        }

        // Calculate traveled distance from previous location
        if (this.previousLocation != null) {
            float distance = (float) location.distance(this.previousLocation);

            // Handle one-time travel distance events
            if (this.travelDistanceEvents != null) {
                this.accumulatedTravelDistance += distance;
                for (var entry : this.travelDistanceEvents.entrySet()) {
                    float threshold = entry.getKey();
                    if (!this.triggeredTravelDistanceThresholds.contains(threshold) && this.accumulatedTravelDistance >= threshold) {
                        if (this.particleInstance != null) {
                            float originalAge = this.particleInstance.get("age");
                            float quotient = Math.round(originalAge / threshold);
                            float agePoint = quotient * threshold;
                            this.particleInstance.set("age", agePoint);
                            for (String event : entry.getValue())
                                this.particleSystem.playEvent(event, this.particleInstance);
                            this.particleInstance.set("age", originalAge);
                        } else {
                            for (String event : entry.getValue())
                                this.particleSystem.playEvent(event, null);
                        }
                        this.triggeredTravelDistanceThresholds.add(threshold);
                    }
                }
            }

            // Handle looping travel distance events
            if (this.loopingTravelDistanceEvents != null) {
                for (LoopingTravelDistanceEvent loopingEvent : this.loopingTravelDistanceEvents) {
                    float progress = this.loopingProgress.getOrDefault(loopingEvent, 0f) + distance;
                    float threshold = loopingEvent.distance();
                    while (progress >= threshold) {
                        if (this.particleInstance != null) {
                            float originalAge = this.particleInstance.get("age");
                            float quotient = Math.round(originalAge / threshold);
                            float agePoint = quotient * threshold;
                            this.particleInstance.set("age", agePoint);
                            for (String event : loopingEvent.effects())
                                this.particleSystem.playEvent(event, this.particleInstance);
                            this.particleInstance.set("age", originalAge);
                        } else {
                            for (String event : loopingEvent.effects())
                                this.particleSystem.playEvent(event, null);
                        }
                        progress -= threshold;
                    }
                    this.loopingProgress.put(loopingEvent, progress);
                }
            }
        }

        this.previousLocation = location;
        this.previousAge = age;
    }

}
