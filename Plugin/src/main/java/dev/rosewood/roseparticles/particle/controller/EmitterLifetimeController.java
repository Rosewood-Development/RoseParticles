package dev.rosewood.roseparticles.particle.controller;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.emitter.lifetime.EmitterLifetimeExpressionComponent;
import dev.rosewood.roseparticles.component.emitter.lifetime.EmitterLifetimeLoopingComponent;
import dev.rosewood.roseparticles.component.emitter.lifetime.EmitterLifetimeOnceComponent;
import dev.rosewood.roseparticles.particle.EmitterInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;

public class EmitterLifetimeController {

    private final ParticleSystem particleSystem;

    private final MolangExpression activationExpression;
    private final MolangExpression expirationExpression;

    private final MolangExpression activeTimeExpression;
    private final MolangExpression sleepTimeExpression;
    private float sleepDuration;
    private boolean sleeping;

    private boolean once;
    private boolean finished;

    public EmitterLifetimeController(ParticleSystem particleSystem, EmitterInstance emitter, EmitterLifetimeExpressionComponent component) {
        this.particleSystem = particleSystem;
        ExpressionBindingContext context = particleSystem.getMolangContext();

        this.activationExpression = component.activationExpression().bind(context, emitter);
        this.expirationExpression = component.expirationExpression().bind(context, emitter);
        this.activeTimeExpression = null;
        this.sleepTimeExpression = null;
    }

    public EmitterLifetimeController(ParticleSystem particleSystem, EmitterInstance emitter, EmitterLifetimeLoopingComponent component) {
        this.particleSystem = particleSystem;
        ExpressionBindingContext context = particleSystem.getMolangContext();

        this.activationExpression = null;
        this.expirationExpression = null;
        this.activeTimeExpression = component.activeTime().bind(context, emitter);
        this.sleepTimeExpression = component.sleepTime().bind(context, emitter);

        emitter.set("lifetime", this.activeTimeExpression.evaluate());
        this.sleepDuration = this.sleepTimeExpression.evaluate();
    }

    public EmitterLifetimeController(ParticleSystem particleSystem, EmitterInstance emitter, EmitterLifetimeOnceComponent component) {
        this.particleSystem = particleSystem;
        ExpressionBindingContext context = particleSystem.getMolangContext();

        this.activationExpression = null;
        this.expirationExpression = null;
        this.activeTimeExpression = null;
        this.sleepTimeExpression = null;

        MolangExpression activeTimeExpression = component.activeTime().bind(context, emitter);
        emitter.set("lifetime", activeTimeExpression.evaluate());
        this.once = true;
    }

    public EmitterLifetimeController(ParticleSystem particleSystem, EmitterInstance emitter) {
        this.particleSystem = particleSystem;
        this.activationExpression = null;
        this.expirationExpression = null;
        this.activeTimeExpression = null;
        this.sleepTimeExpression = null;

        emitter.set("lifetime", 10);
    }

    public boolean emitting() {
        if (this.sleeping)
            return false;

        if (this.activationExpression != null)
            return this.activationExpression.evaluate() != 0;

        return true;
    }

    public boolean expired() {
        if (this.finished)
            return true;

        if (this.expirationExpression != null)
            return this.expirationExpression.evaluate() != 0;

        return false;
    }

    /**
     * @return true if this update caused the age to reset to 0
     */
    public boolean update() {
        EmitterInstance emitter = this.particleSystem.getEmitter();
        float age = emitter.get("age");
        if (emitter.has("lifetime") && age >= emitter.get("lifetime")) {
            if (this.once) {
                this.finished = true;
                return false;
            }

            if (this.sleeping) {
                this.sleeping = false;
                emitter.set("age", 0);
                emitter.set("lifetime", this.activeTimeExpression.evaluate());
                this.sleepDuration = this.sleepTimeExpression.evaluate();
                return true;
            } else if (this.sleepDuration > 0) {
                this.sleeping = true;
                emitter.set("age", age - emitter.get("lifetime"));
            } else {
                emitter.set("age", 0);
                return true;
            }
        }
        return false;
    }

}
