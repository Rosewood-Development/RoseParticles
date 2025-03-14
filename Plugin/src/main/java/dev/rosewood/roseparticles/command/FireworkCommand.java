package dev.rosewood.roseparticles.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseparticles.command.argument.ParticleFileArgumentHandler;
import dev.rosewood.roseparticles.command.argument.RelativeLocationArgumentHandler;
import dev.rosewood.roseparticles.command.argument.WorldArgumentHandler;
import dev.rosewood.roseparticles.manager.LocaleManager;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import dev.rosewood.roseparticles.util.FireworkUtil;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FireworkCommand extends BaseRoseCommand {

    public FireworkCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
        ArgumentsDefinition argumentsDefinition = this.getCommandArguments();
        Argument argument = argumentsDefinition.get(0);

        Argument.SubCommandArgument subCommandArgument = (Argument.SubCommandArgument) argument;
        localeManager.sendCommandMessage(context.getSender(), "command-help-title");
        for (RoseCommand command : subCommandArgument.subCommands()) {
            String descriptionKey = command.getDescriptionKey();
            if (!command.canUse(context.getSender()) || descriptionKey == null)
                continue;

            StringPlaceholders stringPlaceholders = StringPlaceholders.of(
                    "cmd", context.getCommandLabel().toLowerCase(),
                    "subcmd", this.getName(),
                    "args", command.getName().toLowerCase() + " " + command.getParametersString(context),
                    "desc", localeManager.getLocaleMessage(descriptionKey)
            );

            localeManager.sendSimpleCommandMessage(context.getSender(), "command-help-list-description", stringPlaceholders);
        }
    }

    public static class SpawnCommand extends BaseRoseCommand {

        public SpawnCommand(RosePlugin rosePlugin) {
            super(rosePlugin);
        }

        @RoseExecutable
        public void execute(CommandContext context, ParticleFile particleFile, Location location, Integer power) {
            if (power == null)
                power = 1;
            power = Math.clamp(power, 0, 255);

            String identifier = particleFile.description().identifier();
            LocaleManager localeManager = context.getRosePlugin().getManager(LocaleManager.class);

            FireworkUtil.spawnFirework(identifier, location, power);
        }

        @Override
        protected CommandInfo createCommandInfo() {
            return CommandInfo.builder("spawn")
                    .descriptionKey("command-firework-spawn-description")
                    .permission("roseparticles.firework.spawn")
                    .arguments(ArgumentsDefinition.builder()
                            .required("particle", ParticleFileArgumentHandler.INSTANCE)
                            .optional("world", WorldArgumentHandler.INSTANCE, context -> !(context.getSender() instanceof Entity || context.getSender() instanceof BlockCommandSender))
                            .required("location", RelativeLocationArgumentHandler.INSTANCE)
                            .optional("power", ArgumentHandlers.INTEGER)
                            .build())
                    .build();
        }

    }

    public static class GiveCommand extends BaseRoseCommand {

        public GiveCommand(RosePlugin rosePlugin) {
            super(rosePlugin);
        }

        @RoseExecutable
        public void execute(CommandContext context, ParticleFile particleFile, Integer power, Player player) {
            if (power == null)
                power = 1;
            power = Math.clamp(power, 0, 255);

            if (player == null && context.getSender() instanceof Player senderPlayer)
                player = senderPlayer;

            LocaleManager localeManager = context.getRosePlugin().getManager(LocaleManager.class);
            if (player == null) {
                localeManager.sendCommandMessage(context.getSender(), "command-firework-give-target");
                return;
            }

            player.getWorld().dropItem(player.getLocation(), FireworkUtil.createFireworkItem(particleFile, power), item -> {
                item.setPickupDelay(0);
            });
            localeManager.sendCommandMessage(context.getSender(), "command-firework-give-success", StringPlaceholders.of("player", player.getName()));
        }

        @Override
        protected CommandInfo createCommandInfo() {
            return CommandInfo.builder("give")
                    .descriptionKey("command-firework-give-description")
                    .permission("roseparticles.firework.give")
                    .arguments(ArgumentsDefinition.builder()
                            .required("particle", ParticleFileArgumentHandler.INSTANCE)
                            .optional("power", ArgumentHandlers.INTEGER)
                            .optional("player", ArgumentHandlers.PLAYER)
                            .build())
                    .build();
        }

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("firework")
                .descriptionKey("command-firework-description")
                .permission("roseparticles.firework")
                .arguments(ArgumentsDefinition.builder()
                        .optionalSub(
                                new SpawnCommand(this.rosePlugin),
                                new GiveCommand(this.rosePlugin)
                        ))
                .build();
    }

}
