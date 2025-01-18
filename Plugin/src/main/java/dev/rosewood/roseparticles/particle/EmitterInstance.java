package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.particle.controller.EmitterLifetimeController;
import dev.rosewood.roseparticles.particle.curve.Curve;
import dev.rosewood.roseparticles.particle.emitter.Emitter;
import dev.rosewood.roseparticles.particle.emitter.PointEmitter;
import dev.rosewood.roseparticles.util.ParticleUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmitterInstance extends ParticleEffect {

    private final ParticleSystem particleSystem;
    private final Map<String, Curve> curves;

    private final MolangExpression perUpdateExpression;
    private final MolangExpression rateNumParticlesExpression;
    private final MolangExpression rateMaxParticlesExpression;

    private final Emitter emitter;
    private final EmitterLifetimeController lifetimeController;
    private boolean emitOnce;
    private int instantEmitAmount;
    private boolean emitted;

    public EmitterInstance(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;

        ExpressionBindingContext context = particleSystem.getMolangContext();
        this.curves = particleSystem.getCurves().entrySet().stream()
                .map(x -> Map.entry(x.getKey(), x.getValue().bind(context, this)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.set("age", 0);

        var initializationComponent = particleSystem.getComponent(ComponentType.EMITTER_INITIALIZATION);
        if (initializationComponent != null) {
            initializationComponent.creationExpression().bind(context, this).evaluate();
            this.perUpdateExpression = initializationComponent.perUpdateExpression().bind(context, this);
        } else {
            this.perUpdateExpression = null;
        }

        var lifetimeOnceComponent = particleSystem.getComponent(ComponentType.EMITTER_LIFETIME_ONCE);
        var lifetimeLoopingComponent = particleSystem.getComponent(ComponentType.EMITTER_LIFETIME_LOOPING);
        var lifetimeExpressionComponent = particleSystem.getComponent(ComponentType.EMITTER_LIFETIME_EXPRESSION);
        if (lifetimeOnceComponent != null) {
            this.lifetimeController = new EmitterLifetimeController(this.particleSystem, this, lifetimeOnceComponent);
        } else if (lifetimeLoopingComponent != null) {
            this.lifetimeController = new EmitterLifetimeController(this.particleSystem, this, lifetimeLoopingComponent);
        } else if (lifetimeExpressionComponent != null) {
            this.lifetimeController = new EmitterLifetimeController(this.particleSystem, this, lifetimeExpressionComponent);
        } else {
            this.lifetimeController = new EmitterLifetimeController(this.particleSystem);
        }

        Emitter emitter = null;
        var pointEmitterComponent = particleSystem.getComponent(ComponentType.EMITTER_SHAPE_POINT);
        if (pointEmitterComponent != null)
            emitter = new PointEmitter(particleSystem, pointEmitterComponent);

        var rateSteadyComponent = particleSystem.getComponent(ComponentType.EMITTER_RATE_STEADY);
        if (rateSteadyComponent != null) {
            this.rateNumParticlesExpression = rateSteadyComponent.spawnRate().bind(context, this);
            this.rateMaxParticlesExpression = rateSteadyComponent.maxParticles().bind(context, this);
        } else {
            this.rateNumParticlesExpression = null;
            this.rateMaxParticlesExpression = null;
        }

        var rateInstantComponent = particleSystem.getComponent(ComponentType.EMITTER_RATE_INSTANT);
        if (rateInstantComponent != null) {
            this.emitOnce = true;
            this.instantEmitAmount = (int) rateInstantComponent.numParticles().bind(context, this).evaluate();
        }

        this.emitter = emitter;

        for (int i = 1; i <= 4; i++)
            this.set("random_" + i, ParticleUtils.RANDOM.nextFloat());

        for (var curveEntry : this.curves.entrySet())
            this.set(curveEntry.getKey(), curveEntry.getValue().evaluate());
    }

    public List<ParticleInstance> update(float deltaTime) {
        if (this.perUpdateExpression != null)
            this.perUpdateExpression.evaluate();

        for (var curveEntry : this.curves.entrySet())
            this.set(curveEntry.getKey(), curveEntry.getValue().evaluate());

        List<ParticleInstance> emission = new ArrayList<>();
        if (this.emitOnce && !this.emitted && this.lifetimeController.emitting()) {
            for (int i = 0; i < this.instantEmitAmount; i++) {
                emission.add(this.emitter.emit());
            }
            this.set("age", this.get("age") + deltaTime);
            this.emitted = true;
        } else if (this.rateNumParticlesExpression != null) {
            int rateNumParticles = Math.round(this.rateNumParticlesExpression.evaluate() * deltaTime);
            int rateMaxParticles = (int) this.rateMaxParticlesExpression.evaluate();

            int particleCount = this.particleSystem.getParticleCount();
            if (this.lifetimeController.emitting()) {
                float deltaStepSize = deltaTime / rateNumParticles;
                int i = 0;
                for (; i < rateNumParticles; i++) {
                    this.set("age", this.get("age") + deltaStepSize);
                    emission.add(this.emitter.emit());
                    if (particleCount >= rateMaxParticles)
                        break;
                }

                if (i != rateNumParticles)
                    this.set("age", this.get("age") + (deltaStepSize * (rateNumParticles - i)));
            } else {
                this.set("age", this.get("age") + deltaTime);
            }
        }

        this.lifetimeController.update();

        return emission;
    }

    public boolean expired() {
        if (this.lifetimeController.expired())
            return true;

        if (this.has("lifetime") && this.get("age") >= this.get("lifetime"))
            return true;

        if (this.emitter == null)
            return true;

        return false;
    }

    @Override
    public float get(String identifier) {
        return super.get(mapIdentifier(identifier));
    }

    @Override
    public boolean has(String identifier) {
        return super.has(mapIdentifier(identifier));
    }

    @Override
    public void set(String identifier, float value) {
        super.set(mapIdentifier(identifier), value);
    }

    private static String mapIdentifier(String identifier) {
        if (identifier.startsWith("emitter_"))
            return identifier.substring("emitter_".length());
        return identifier;
    }

}
