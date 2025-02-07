package dev.rosewood.roseparticles.particle.emitter;

import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeBoxComponent;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import org.bukkit.util.Vector;

public class BoxEmitter extends BaseBoxEmitter {

    private final MolangExpressionVector3 offset;
    private final MolangExpressionVector3 halfDimensions;

    public BoxEmitter(ParticleSystem particleSystem, EmitterShapeBoxComponent component) {
        super(particleSystem, component.surfaceOnly(), component.direction(), component.directionCustom());
        this.offset = component.offset();
        this.halfDimensions = component.halfDimensions();
    }

    @Override
    protected BoxParameters getParameters(ParticleInstance particleInstance) {
        Vector offset = this.offset.bind(this.particleSystem.getMolangContext(), particleInstance, this.particleSystem.getEmitter()).evaluate();
        Vector halfDimensions = this.halfDimensions.bind(this.particleSystem.getMolangContext(), particleInstance, this.particleSystem.getEmitter()).evaluate();
        return new BoxParameters(offset, halfDimensions);
    }

}
