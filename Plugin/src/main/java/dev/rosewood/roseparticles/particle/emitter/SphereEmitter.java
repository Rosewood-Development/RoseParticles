package dev.rosewood.roseparticles.particle.emitter;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeSphereComponent;
import dev.rosewood.roseparticles.component.model.EmitterDirectionType;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.particle.EmitterInstance;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import dev.rosewood.roseparticles.util.ParticleUtils;
import org.bukkit.util.Vector;

public class SphereEmitter implements Emitter {

    private final ParticleSystem particleSystem;
    private final MolangExpressionVector3 offset;
    private final MolangExpression radius;
    private final boolean surfaceOnly;
    private final EmitterDirectionType direction;
    private final MolangExpressionVector3 directionCustom;

    public SphereEmitter(ParticleSystem particleSystem, EmitterShapeSphereComponent component) {
        this.particleSystem = particleSystem;
        this.offset = component.offset();
        this.radius = component.radius();
        this.surfaceOnly = component.surfaceOnly();
        this.direction = component.direction();
        this.directionCustom = component.directionCustom();
    }

    @Override
    public ParticleInstance emit() {
        ExpressionBindingContext context = this.particleSystem.getMolangContext();
        EmitterInstance emitterInstance = this.particleSystem.getEmitter();
        ParticleInstance particleInstance = new ParticleInstance(this.particleSystem);
        Vector offset = this.offset.bind(context, particleInstance, emitterInstance).evaluate();
        float radius = this.radius.bind(context, particleInstance, emitterInstance).evaluate();

        Vector randomDir = new Vector(
                ParticleUtils.RANDOM.nextGaussian(),
                ParticleUtils.RANDOM.nextGaussian(),
                ParticleUtils.RANDOM.nextGaussian()
        ).normalize();

        float distance = this.surfaceOnly ? radius : (float) (radius * Math.cbrt(ParticleUtils.RANDOM.nextFloat()));

        randomDir.multiply(distance);
        Vector particlePosition = offset.clone().add(randomDir);

        Vector particleDirection = switch (this.direction) {
            case OUTWARDS -> particlePosition.clone().subtract(offset).normalize();
            case INWARDS -> offset.clone().subtract(particlePosition).normalize();
            case VELOCITY -> this.directionCustom.bind(context, particleInstance, emitterInstance).evaluate();
        };

        particleInstance.init(particlePosition, particleDirection);
        return particleInstance;
    }

}
