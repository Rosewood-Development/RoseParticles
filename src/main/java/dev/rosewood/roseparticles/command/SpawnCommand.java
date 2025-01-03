package dev.rosewood.roseparticles.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseparticles.manager.ParticleManager;
import dev.rosewood.roseparticles.particle.EmitterInstance;
import dev.rosewood.roseparticles.particle.ParticleEffect;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpawnCommand extends BaseRoseCommand {

    public SpawnCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();
        ParticleManager particleManager = this.rosePlugin.getManager(ParticleManager.class);
        ParticleSystem particleSystem = new ParticleSystem(player.getLocation());
        ParticleEffect emitter = new EmitterInstance(200, () -> {
            List<ParticleEffect> effects = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Vector velocity = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5));
                Vector acceleration = new Vector(0, -0.0784, 0);
                List<String> sprites = List.of("\u0001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006", "\u0007", "\u0008").reversed();
                ParticleEffect particleEffect = new ParticleInstance(60, velocity, acceleration, "roseparticles:sprites", sprites);
                effects.add(particleEffect);
            }
            return effects;
        });
        particleSystem.addParticle(emitter);
        particleManager.spawnParticleSystem(particleSystem);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("spawn")
                .playerOnly()
                .build();
    }

}
