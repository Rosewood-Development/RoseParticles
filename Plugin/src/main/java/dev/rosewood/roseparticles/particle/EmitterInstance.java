package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.particle.controller.EmitterLifetimeController;
import dev.rosewood.roseparticles.particle.controller.LifetimeEventController;
import dev.rosewood.roseparticles.particle.curve.Curve;
import dev.rosewood.roseparticles.particle.emitter.BoxEmitter;
import dev.rosewood.roseparticles.particle.emitter.DiscEmitter;
import dev.rosewood.roseparticles.particle.emitter.Emitter;
import dev.rosewood.roseparticles.particle.emitter.EntityAABBEmitter;
import dev.rosewood.roseparticles.particle.emitter.PointEmitter;
import dev.rosewood.roseparticles.particle.emitter.SphereEmitter;
import dev.rosewood.roseparticles.util.ParticleUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.util.Vector;

public class EmitterInstance extends ParticleEffect {

    private final ParticleSystem particleSystem;
    private final Map<String, Curve> curves;
    private float[] randoms;

    private final MolangExpression perUpdateExpression;
    private final MolangExpression rateNumParticlesExpression;
    private final MolangExpression rateMaxParticlesExpression;

    private final Emitter emitter;
    private final EmitterLifetimeController lifetimeController;
    private final LifetimeEventController lifetimeEventController;
    
    private boolean emitOnce;
    private int instantEmitAmount;
    private boolean emitted;
    private float particlePercentage;
    private float lastParticleAge;

    public EmitterInstance(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;

        ExpressionBindingContext context = particleSystem.getMolangContext();
        this.curves = particleSystem.getCurves().entrySet().stream()
                .map(x -> Map.entry(x.getKey(), x.getValue().bind(context, this)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.set("age", 0);

        this.randoms = new float[]{ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat()};

        var initialization = particleSystem.getComponent(ComponentType.EMITTER_INITIALIZATION);
        if (initialization != null) {
            initialization.creationExpression().bind(context, this).evaluate();
            this.perUpdateExpression = initialization.perUpdateExpression().bind(context, this);
        } else {
            this.perUpdateExpression = null;
        }

        var lifetimeOnce = particleSystem.getComponent(ComponentType.EMITTER_LIFETIME_ONCE);
        var lifetimeLooping = particleSystem.getComponent(ComponentType.EMITTER_LIFETIME_LOOPING);
        var lifetimeExpression = particleSystem.getComponent(ComponentType.EMITTER_LIFETIME_EXPRESSION);
        if (lifetimeOnce != null) {
            this.lifetimeController = new EmitterLifetimeController(this.particleSystem, this, lifetimeOnce);
        } else if (lifetimeLooping != null) {
            this.lifetimeController = new EmitterLifetimeController(this.particleSystem, this, lifetimeLooping);
        } else if (lifetimeExpression != null) {
            this.lifetimeController = new EmitterLifetimeController(this.particleSystem, this, lifetimeExpression);
        } else {
            this.lifetimeController = new EmitterLifetimeController(this.particleSystem, this);
        }

        var pointEmitter = particleSystem.getComponent(ComponentType.EMITTER_SHAPE_POINT);
        var customEmitter = particleSystem.getComponent(ComponentType.EMITTER_SHAPE_CUSTOM);
        var discEmitter = particleSystem.getComponent(ComponentType.EMITTER_SHAPE_DISC);
        var sphereEmitter = particleSystem.getComponent(ComponentType.EMITTER_SHAPE_SPHERE);
        var boxEmitter = particleSystem.getComponent(ComponentType.EMITTER_SHAPE_BOX);
        var entityAABBEmitter = particleSystem.getComponent(ComponentType.EMITTER_SHAPE_ENTITY_AABB);
        if (pointEmitter != null) {
            this.emitter = new PointEmitter(particleSystem, pointEmitter);
        } else if (customEmitter != null) {
            this.emitter = new PointEmitter(particleSystem, customEmitter);
        } else if (discEmitter != null) {
            this.emitter = new DiscEmitter(particleSystem, discEmitter);
        } else if (sphereEmitter != null) {
            this.emitter = new SphereEmitter(particleSystem, sphereEmitter);
        } else if (boxEmitter != null) {
            this.emitter = new BoxEmitter(particleSystem, boxEmitter);
        } else if (entityAABBEmitter != null) {
            this.emitter = new EntityAABBEmitter(particleSystem, entityAABBEmitter);
        } else {
            throw new IllegalArgumentException("No emitter component");
        }

        MolangExpression rateMaxParticlesExpression = null;

        var rateSteady = particleSystem.getComponent(ComponentType.EMITTER_RATE_STEADY);
        if (rateSteady != null) {
            this.rateNumParticlesExpression = rateSteady.spawnRate().bind(context, this);
            rateMaxParticlesExpression = rateSteady.maxParticles().bind(context, this);
        } else {
            this.rateNumParticlesExpression = null;
        }

        var rateManual = particleSystem.getComponent(ComponentType.EMITTER_RATE_MANUAL);
        if (rateManual != null)
            rateMaxParticlesExpression = rateManual.maxParticles().bind(context, this);

        this.rateMaxParticlesExpression = rateMaxParticlesExpression;

        var rateInstant = particleSystem.getComponent(ComponentType.EMITTER_RATE_INSTANT);
        if (rateInstant != null) {
            this.emitOnce = true;
            this.instantEmitAmount = (int) rateInstant.numParticles().bind(context, this).evaluate();
        }

        for (int i = 1; i <= 4; i++)
            this.set("random_" + i, ParticleUtils.RANDOM.nextFloat());

        for (var curveEntry : this.curves.entrySet())
            this.set(curveEntry.getKey(), curveEntry.getValue().evaluate());

        var lifetimeEvents = particleSystem.getComponent(ComponentType.EMITTER_LIFETIME_EVENTS);
        if (lifetimeEvents != null) {
            this.lifetimeEventController = new LifetimeEventController(particleSystem, lifetimeEvents);
        } else {
            this.lifetimeEventController = null;
        }
    }

    public List<ParticleInstance> update(float deltaTime) {
        if (this.perUpdateExpression != null)
            this.perUpdateExpression.evaluate();

        for (var curveEntry : this.curves.entrySet())
            this.set(curveEntry.getKey(), curveEntry.getValue().evaluate());

        if (this.lifetimeEventController != null && this.get("age") == 0)
            this.lifetimeEventController.onCreation();

        List<ParticleInstance> emission = new ArrayList<>();
        if (this.emitOnce && !this.emitted && this.lifetimeController.emitting()) {
            for (int i = 0; i < this.instantEmitAmount; i++) {
                emission.add(this.emitter.emit());
            }
            this.set("age", this.get("age") + deltaTime);
            this.emitted = true;
        } else if (this.rateNumParticlesExpression != null) {
            float rateNumParticles = this.rateNumParticlesExpression.evaluate();
            float rateMaxParticles = this.rateMaxParticlesExpression.evaluate();

            int particleCount = this.particleSystem.getParticleCount();
            if (this.lifetimeController.emitting()) {
                float age = this.get("age");
                this.particlePercentage += rateNumParticles * deltaTime;
                int particlesToSpawn = (int) this.particlePercentage;
                this.particlePercentage -= particlesToSpawn;
                float interval = 1 / rateNumParticles;
                for (int i = 0; i < particlesToSpawn; i++) {
                    if (particleCount >= rateMaxParticles) {
                        // Skip remaining but still account for the age increments
                        this.lastParticleAge += (particlesToSpawn - i) * interval;
                        break;
                    }
                    float nextAge = this.lastParticleAge + interval;
                    this.set("age", nextAge);
                    this.lastParticleAge = nextAge;
                    emission.add(this.emitter.emit());
                    particleCount++;
                }
                this.set("age", age + deltaTime);
            } else {
                this.set("age", this.get("age") + deltaTime);
            }
        } else {
            this.set("age", this.get("age") + deltaTime);
        }

        // Reset emitter state if we are looping
        if (this.lifetimeController.update()) {
            this.lastParticleAge = 0;
            this.particlePercentage = 0;
            this.emitted = false;
            this.randoms = new float[]{ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat()};
        }

        if (this.lifetimeEventController != null)
            this.lifetimeEventController.update();

        return emission;
    }

    public ParticleInstance emitManually(Vector velocity) {
        if (this.rateMaxParticlesExpression != null) {
            float rateMaxParticles = this.rateMaxParticlesExpression.evaluate();
            if (this.particleSystem.getParticleCount() >= rateMaxParticles)
                return null;
        }

        ParticleInstance particleInstance = this.emitter.emit();
        if (velocity != null)
            particleInstance.setVelocity(velocity);

        this.particleSystem.addParticle(particleInstance);
        return particleInstance;
    }

    public boolean expired() {
        boolean expired = this.lifetimeController.expired();

        if (!expired && this.has("lifetime") && this.get("age") >= this.get("lifetime"))
            expired = true;

        if (!expired && this.emitter == null)
            expired = true;

        if (expired && this.lifetimeEventController != null)
            this.lifetimeEventController.onExpiration();

        return expired;
    }

    @Override
    public float get(String identifier) {
        return switch (identifier) {
            case "emitter_random_1" -> this.randoms[0];
            case "emitter_random_2" -> this.randoms[1];
            case "emitter_random_3" -> this.randoms[2];
            case "emitter_random_4" -> this.randoms[3];
            default -> super.get(mapIdentifier(identifier));
        };
    }

    @Override
    public boolean has(String identifier) {
        return switch (identifier) {
            case "emitter_random_1", "emitter_random_2", "emitter_random_3", "emitter_random_4" -> true;
            default -> super.has(mapIdentifier(identifier));
        };
    }

    @Override
    public void set(String identifier, float value) {
        switch (identifier) {
            case "emitter_random_1", "emitter_random_2", "emitter_random_3", "emitter_random_4" -> {}
            default -> super.set(mapIdentifier(identifier), value);
        };
        super.set(mapIdentifier(identifier), value);
    }

    private static String mapIdentifier(String identifier) {
        if (identifier.startsWith("emitter_"))
            return identifier.substring(8);
        return identifier;
    }

}
