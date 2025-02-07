package dev.rosewood.roseparticles.particle.emitter;

import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeCustomComponent;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapePointComponent;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.particle.EmitterInstance;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import org.bukkit.util.Vector;

public class PointEmitter implements Emitter {

    private final ParticleSystem particleSystem;
    private final MolangExpressionVector3 offset;
    private final MolangExpressionVector3 direction;

    public PointEmitter(ParticleSystem particleSystem, EmitterShapePointComponent component) {
        this.particleSystem = particleSystem;
        this.offset = component.offset();
        this.direction = component.direction();
    }

    public PointEmitter(ParticleSystem particleSystem, EmitterShapeCustomComponent component) {
        this.particleSystem = particleSystem;
        this.offset = component.offset();
        this.direction = component.direction();
    }

    @Override
    public ParticleInstance emit() {
        ExpressionBindingContext context = this.particleSystem.getMolangContext();
        EmitterInstance emitterInstance = this.particleSystem.getEmitter();
        ParticleInstance particleInstance = new ParticleInstance(this.particleSystem);
        Vector offset = this.offset.bind(context, particleInstance, emitterInstance).evaluate();
        Vector direction = this.direction.bind(context, particleInstance, emitterInstance).evaluate();
        particleInstance.init(offset, direction);
        return particleInstance;
    }

}
