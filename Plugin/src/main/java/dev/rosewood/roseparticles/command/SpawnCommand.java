package dev.rosewood.roseparticles.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseparticles.command.argument.ParticleFileArgumentHandler;
import dev.rosewood.roseparticles.manager.ParticleManager;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseRoseCommand {

    public SpawnCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, ParticleFile particleFile) {
        Location location;
        if (context.getSender() instanceof Player player) {
            location = player.getLocation();
        } else {
            location = new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0);
        }
        ParticleManager particleManager = this.rosePlugin.getManager(ParticleManager.class);
        particleManager.spawnParticleSystem(location, particleFile);
//        ParticleSystem particleSystem = new ParticleSystem(player.getLocation());
//        ParticleEffect emitter = new EmitterInstance(200, () -> {
//            List<ParticleEffect> effects = new ArrayList<>();
//            for (int i = 0; i < 10; i++) {
//                Vector velocity = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5));
//                Vector acceleration = new Vector(0, -0.0784, 0);
//                List<String> sprites = List.of("\u0001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006", "\u0007", "\u0008").reversed();
//                ParticleEffect particleEffect = new ParticleInstance(60, velocity, acceleration, "roseparticles:sprites", sprites);
//                effects.add(particleEffect);
//            }
//            return effects;
//        });
//        particleSystem.addParticle(emitter);
//        particleManager.spawnParticleSystem(particleSystem);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("spawn")
                .descriptionKey("spawn-command-description")
                .permission("roseparticles.spawn")
                .arguments(ArgumentsDefinition.builder()
                        .required("particle", ParticleFileArgumentHandler.INSTANCE)
                        .build())
                .build();
    }

}
