package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.ast.ConstantExpression;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.util.ParticleUtils;

public class EmitterInstance extends ParticleEffect {

    private final ParticleSystem particleSystem;

    private final MolangExpression perUpdateExpression;
    private final MolangExpression activationExpression;
    private final MolangExpression expirationExpression;

    public EmitterInstance(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
        this.set("age", 0);

        ExpressionBindingContext context = particleSystem.getMolangContext();

        var initializationComponent = particleSystem.getComponent(ComponentType.EMITTER_INITIALIZATION);
        if (initializationComponent != null) {
            initializationComponent.creationExpression().bind(context, this).evaluate();
            this.perUpdateExpression = initializationComponent.perUpdateExpression().bind(context, this);
        } else {
            this.perUpdateExpression = null;
        }

        var lifetimeExpressionComponent = particleSystem.getComponent(ComponentType.EMITTER_LIFETIME_EXPRESSION);
        if (lifetimeExpressionComponent != null) {
            this.activationExpression = lifetimeExpressionComponent.activationExpression().bind(context, this);
            this.expirationExpression = lifetimeExpressionComponent.expirationExpression().bind(context, this);
        } else {
            this.activationExpression = new ConstantExpression(1);
            this.expirationExpression = null;
        }

        for (int i = 1; i <= 4; i++)
            this.set("random_" + i, ParticleUtils.RANDOM.nextFloat());
    }

    @Override
    public void update(float deltaTime) {
        this.set("age", this.get("age") + deltaTime);

        if (this.perUpdateExpression != null)
            this.perUpdateExpression.evaluate();
    }

    public boolean expired() {
        if (this.expirationExpression != null)
            if (this.expirationExpression.evaluate() != 0)
                return true;

        if (this.has("lifetime"))
            return this.get("age") >= this.get("lifetime");

        return false;
    }

}
