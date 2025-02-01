package dev.rosewood.roseparticles.particle.emitter;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeDiscComponent;
import dev.rosewood.roseparticles.component.model.EmitterDirectionType;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.particle.EmitterInstance;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import dev.rosewood.roseparticles.util.ParticleUtils;
import org.bukkit.util.Vector;

public class DiscEmitter implements Emitter {

    private final ParticleSystem particleSystem;
    private final MolangExpressionVector3 offset;
    private final MolangExpression radius;
    private final MolangExpressionVector3 planeNormal;
    private final boolean surfaceOnly;
    private final EmitterDirectionType direction;
    private final MolangExpressionVector3 directionCustom;

    public DiscEmitter(ParticleSystem particleSystem, EmitterShapeDiscComponent component) {
        this.particleSystem = particleSystem;
        this.offset = component.offset();
        this.radius = component.radius();
        this.planeNormal = component.planeNormalCustom();
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
        Vector planeNormal = this.planeNormal.bind(context, particleInstance, emitterInstance).evaluate();

        if (planeNormal.getX() == 0 && planeNormal.getY() == 0 && planeNormal.getZ() == 0)
            planeNormal.setY(1);

        if (!this.surfaceOnly)
            radius *= (float) ParticleUtils.RANDOM.nextDouble();

        Vector tangent;
        if (planeNormal.getX() == 0 && planeNormal.getY() == 0) {
            tangent = new Vector(0, -planeNormal.getZ(), planeNormal.getY()).normalize();
        } else {
            tangent = new Vector(-planeNormal.getY(), planeNormal.getX(), 0).normalize();
        }

        Vector w = planeNormal.clone().crossProduct(tangent).normalize();
        double angle = ParticleUtils.RANDOM.nextDouble() * Math.PI * 2;
        double x = Math.cos(angle) * radius;
        double z = Math.sin(angle) * radius;

        Vector particlePosition = w.multiply(x).add(tangent.multiply(z)).add(offset);

        Vector particleDirection = switch (this.direction) {
            case OUTWARDS -> particlePosition.clone().subtract(this.particleSystem.getOrigin().toVector()).normalize();
            case INWARDS -> this.particleSystem.getOrigin().toVector().subtract(particlePosition).normalize();
            case VELOCITY -> this.directionCustom.bind(context, particleInstance, emitterInstance).evaluate();
        };

        particleInstance.init(particlePosition, particleDirection);
        return particleInstance;
    }

}
