package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.component.emitter.init.EmitterInitializationComponent;
import dev.rosewood.roseparticles.util.ParticleUtils;
import org.bukkit.Bukkit;

public class EmitterInstance implements ParticleEffect {

    private final ParticleSystem particleSystem;
    private float age;
    private final float[] randoms;

    public EmitterInstance(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
        this.randoms = new float[]{ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat()};

        EmitterInitializationComponent initializationComponent = particleSystem.getComponent(ComponentType.EMITTER_INITIALIZATION);
        if (initializationComponent != null) {
            MolangSession molangSession = particleSystem.getMolangSession();
            ExpressionBindingContext context = molangSession.getContext();
            initializationComponent.creationExpression().bind(context, this).evaluate();
            try {
                float tempResult = MolangParser.parse("variable.radius").bind(context, this).evaluate();
                Bukkit.broadcastMessage(String.valueOf(tempResult));
            } catch (MolangLexException | MolangParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void update() {
        this.age++;
    }

    public float getLifetime() {
        return 0;
    }

    public float getAge() {
        return this.age;
    }

    public float[] getRandoms() {
        return this.randoms;
    }

}
