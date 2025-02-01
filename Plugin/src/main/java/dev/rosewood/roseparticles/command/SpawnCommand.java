package dev.rosewood.roseparticles.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseparticles.command.argument.ParticleFileArgumentHandler;
import dev.rosewood.roseparticles.manager.ParticleManager;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseRoseCommand {

    public SpawnCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, ParticleFile particleFile, String entitySelector) {
        ParticleManager particleManager = this.rosePlugin.getManager(ParticleManager.class);
        if (entitySelector != null) {
            List<Entity> entities = Bukkit.selectEntities(context.getSender(), entitySelector);
            for (Entity entity : entities)
                particleManager.spawnParticleSystem(entity, particleFile);
        } else if (context.getSender() instanceof Player player) {
            Location location = player.getLocation();
            particleManager.spawnParticleSystem(location, particleFile);
        } else {
            Location location = new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0);
            particleManager.spawnParticleSystem(location, particleFile);
        }
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("spawn")
                .descriptionKey("spawn-command-description")
                .permission("roseparticles.spawn")
                .arguments(ArgumentsDefinition.builder()
                        .required("particle", ParticleFileArgumentHandler.INSTANCE)
                        .optional("entity", ArgumentHandlers.STRING)
                        .build())
                .build();
    }

}
