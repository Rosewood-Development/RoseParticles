package dev.rosewood.roseparticles.particle.emitter;

import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapePointComponent;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.particle.EmitterInstance;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import org.bukkit.util.Vector;

public class PointEmitter implements Emitter {

    private final ParticleSystem particleSystem;
    private final MolangExpressionVector3 offsetExpression;
    private final MolangExpressionVector3 directionExpression;

    public PointEmitter(ParticleSystem particleSystem, EmitterShapePointComponent component) {
        this.particleSystem = particleSystem;
        this.offsetExpression = component.offset();
        this.directionExpression = component.direction();
    }

    @Override
    public ParticleInstance emit() {
        ExpressionBindingContext context = this.particleSystem.getMolangContext();
        EmitterInstance emitterInstance = this.particleSystem.getEmitter();
        ParticleInstance particleInstance = new ParticleInstance(this.particleSystem);
        Vector offset = this.offsetExpression.bind(context, particleInstance, emitterInstance).evaluate();
        Vector direction = this.directionExpression.bind(context, particleInstance, emitterInstance).evaluate();
        particleInstance.init(offset, direction);
        return particleInstance;
    }

}
