package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.component.emitter.init.EmitterInitializationComponent;
import dev.rosewood.roseparticles.util.ParticleUtils;

public class EmitterInstance extends ParticleEffect {

    private final ParticleSystem particleSystem;

    public EmitterInstance(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
        this.set("age", 0);

        ExpressionBindingContext context = particleSystem.getMolangContext();

        EmitterInitializationComponent initializationComponent = particleSystem.getComponent(ComponentType.EMITTER_INITIALIZATION);
        if (initializationComponent != null) {
            initializationComponent.creationExpression().bind(context, this).evaluate();
        }

        for (int i = 1; i <= 4; i++)
            this.set("random_" + i, ParticleUtils.RANDOM.nextFloat());
    }

    @Override
    public void update() {
        this.set("age", this.get("age") + 1);
    }

}
