package dev.rosewood.roseparticles.component.event;

import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseparticles.RoseParticles;
import dev.rosewood.roseparticles.component.model.ParticleEffectType;
import dev.rosewood.roseparticles.manager.ParticleManager;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import dev.rosewood.roseparticles.util.JsonHelper;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public record ParticleEvent(String effect,
                            ParticleEffectType type,
                            MolangExpression preEffectExpression) {

    public static ParticleEvent parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        String effect = jsonObject.get("effect").getAsString();
        ParticleEffectType type = ParticleEffectType.parse(jsonObject, "type");
        MolangExpression preEffectExpression = JsonHelper.parseMolang(jsonObject, "pre_effect_expression", null);
        return new ParticleEvent(effect, type, preEffectExpression);
    }

    public void play(ParticleSystem particleSystem, ParticleInstance particleInstance) {
        RosePlugin rosePlugin = RoseParticles.getInstance(); // yuck
        ParticleManager particleManager = rosePlugin.getManager(ParticleManager.class);

        NamespacedKey key = NamespacedKey.fromString(this.effect.toLowerCase(), rosePlugin);
        ParticleFile particleFile = particleManager.getParticleFiles().get(key);
        if (particleFile == null) {
            RoseParticles.getInstance().getLogger().warning(particleSystem.getIdentifier() + " tried to spawn a particle effect that doesn't exist: " + this.effect.toLowerCase());
            return;
        }

        switch (this.type) {
            case EMITTER -> {
                Location location = particleInstance == null ? particleSystem.getOrigin() : particleInstance.getLocation();
                ParticleSystem newSystem = particleManager.spawnParticleSystem(location, particleFile);
                if (this.preEffectExpression != null)
                    this.preEffectExpression.bind(newSystem.getMolangContext(), newSystem.getEmitter()).evaluate();
            }
            case EMITTER_BOUND -> {
                ParticleSystem newSystem;
                Entity attachedTo = particleSystem.getAttachedTo();
                if (attachedTo != null) {
                    newSystem = particleManager.spawnParticleSystem(attachedTo, particleFile);
                } else {
                    Location location = particleInstance == null ? particleSystem.getOrigin() : particleInstance.getLocation();
                    newSystem = particleManager.spawnParticleSystem(location, particleFile);
                }

                if (this.preEffectExpression != null)
                    this.preEffectExpression.bind(newSystem.getMolangContext(), newSystem.getEmitter()).evaluate();
            }
            case PARTICLE -> {
                ParticleInstance spawnedParticle = particleSystem.getEmitter().emitManually(null);
                if (this.preEffectExpression != null)
                    this.preEffectExpression.bind(particleSystem.getMolangContext(), particleSystem.getEmitter(), spawnedParticle);
            }
            case PARTICLE_WITH_VELOCITY -> {
                Vector velocity = particleInstance == null ? new Vector() : particleInstance.getVelocity();
                ParticleInstance spawnedParticle = particleSystem.getEmitter().emitManually(velocity);
                if (this.preEffectExpression != null)
                    this.preEffectExpression.bind(particleSystem.getMolangContext(), particleSystem.getEmitter(), spawnedParticle);
            }
        }
    }

}
