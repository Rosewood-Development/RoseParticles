package dev.rosewood.roseparticles.particle.emitter;

import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.model.EmitterDirectionType;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.particle.EmitterInstance;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import dev.rosewood.roseparticles.util.ParticleUtils;
import org.bukkit.util.Vector;

public abstract class BaseBoxEmitter implements Emitter {

    protected final ParticleSystem particleSystem;
    private final boolean surfaceOnly;
    private final EmitterDirectionType direction;
    private final MolangExpressionVector3 directionCustom;

    public BaseBoxEmitter(ParticleSystem particleSystem, boolean surfaceOnly, EmitterDirectionType direction, MolangExpressionVector3 directionCustom) {
        this.particleSystem = particleSystem;
        this.surfaceOnly = surfaceOnly;
        this.direction = direction;
        this.directionCustom = directionCustom;
    }

    protected abstract BoxParameters getParameters(ParticleInstance particleInstance);

    @Override
    public ParticleInstance emit() {
        ExpressionBindingContext context = this.particleSystem.getMolangContext();
        ParticleInstance particleInstance = new ParticleInstance(this.particleSystem);
        EmitterInstance emitterInstance = this.particleSystem.getEmitter();
        BoxParameters parameters = this.getParameters(particleInstance);
        Vector offset = parameters.offset();
        Vector halfDimensions = parameters.halfDimensions();

        Vector particlePosition;

        if (this.surfaceOnly) {
            double halfX = halfDimensions.getX();
            double halfY = halfDimensions.getY();
            double halfZ = halfDimensions.getZ();

            double xArea = 8 * halfY * halfZ;
            double yArea = 8 * halfX * halfZ;
            double zArea = 8 * halfX * halfY;
            double totalArea = xArea + yArea + zArea;
            double randomValue = ParticleUtils.RANDOM.nextDouble() * totalArea;

            double x, y, z;
            if (randomValue < xArea) {
                boolean positive = ParticleUtils.RANDOM.nextDouble() < 0.5;
                x = offset.getX() + (positive ? halfX : -halfX);
                y = offset.getY() + (ParticleUtils.RANDOM.nextDouble() * 2 - 1) * halfY;
                z = offset.getZ() + (ParticleUtils.RANDOM.nextDouble() * 2 - 1) * halfZ;
            } else if (randomValue < xArea + yArea) {
                boolean positive = ParticleUtils.RANDOM.nextDouble() < 0.5;
                y = offset.getY() + (positive ? halfY : -halfY);
                x = offset.getX() + (ParticleUtils.RANDOM.nextDouble() * 2 - 1) * halfX;
                z = offset.getZ() + (ParticleUtils.RANDOM.nextDouble() * 2 - 1) * halfZ;
            } else {
                boolean positive = ParticleUtils.RANDOM.nextDouble() < 0.5;
                z = offset.getZ() + (positive ? halfZ : -halfZ);
                x = offset.getX() + (ParticleUtils.RANDOM.nextDouble() * 2 - 1) * halfX;
                y = offset.getY() + (ParticleUtils.RANDOM.nextDouble() * 2 - 1) * halfY;
            }

            particlePosition = new Vector(x, y, z);
        } else {
            double x = offset.getX() + (Math.random() * 2 - 1) * halfDimensions.getX();
            double y = offset.getY() + (Math.random() * 2 - 1) * halfDimensions.getY();
            double z = offset.getZ() + (Math.random() * 2 - 1) * halfDimensions.getZ();
            particlePosition = new Vector(x, y, z);
        }

        Vector particleDirection = switch (this.direction) {
            case OUTWARDS -> particlePosition.clone().subtract(offset).normalize();
            case INWARDS -> offset.clone().subtract(particlePosition).normalize();
            case VELOCITY -> this.directionCustom.bind(context, particleInstance, emitterInstance).evaluate();
        };

        particleInstance.init(particlePosition, particleDirection);
        return particleInstance;
    }

    protected record BoxParameters(Vector offset, Vector halfDimensions) { }

}
