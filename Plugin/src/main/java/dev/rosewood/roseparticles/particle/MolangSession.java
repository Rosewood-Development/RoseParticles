package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.ast.ObjectAwareExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.omega.arcane.reference.ReferenceType;
import java.util.function.Function;

public class MolangSession {

    private final ExpressionBindingContext bindingContext;

    public MolangSession() {
        this.bindingContext = ExpressionBindingContext.create();
        this.registerVariable("emitter_lifetime", EmitterInstance.class, EmitterInstance::getLifetime);
        this.registerVariable("emitter_age", EmitterInstance.class, EmitterInstance::getAge);
        this.registerVariable("emitter_random_1", EmitterInstance.class, emitter -> emitter.getRandoms()[0]);
        this.registerVariable("emitter_random_2", EmitterInstance.class, emitter -> emitter.getRandoms()[1]);
        this.registerVariable("emitter_random_3", EmitterInstance.class, emitter -> emitter.getRandoms()[2]);
        this.registerVariable("emitter_random_4", EmitterInstance.class, emitter -> emitter.getRandoms()[3]);
        this.registerVariable("particle_lifetime", ParticleInstance.class, ParticleInstance::getLifetime);
        this.registerVariable("particle_age", ParticleInstance.class, ParticleInstance::getAge);
        this.registerVariable("particle_random_1", ParticleInstance.class, particle -> particle.getRandoms()[0]);
        this.registerVariable("particle_random_2", ParticleInstance.class, particle -> particle.getRandoms()[1]);
        this.registerVariable("particle_random_3", ParticleInstance.class, particle -> particle.getRandoms()[2]);
        this.registerVariable("particle_random_4", ParticleInstance.class, particle -> particle.getRandoms()[3]);
    }

    public ExpressionBindingContext getContext() {
        return this.bindingContext;
    }

    private <T> void registerVariable(String name, Class<T> type, Function<T, Float> supplier) {
        this.bindingContext.registerReferenceResolver(
                ReferenceType.VARIABLE,
                name,
                type,
                object -> new ObjectAwareExpression<>(object) {
                    @Override
                    public float evaluate() {
                        return supplier.apply(object);
                    }
                }
        );
    }

}
