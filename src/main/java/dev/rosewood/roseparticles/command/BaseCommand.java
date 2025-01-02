package dev.rosewood.roseparticles.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.HelpCommand;
import dev.rosewood.rosegarden.command.PrimaryCommand;
import dev.rosewood.rosegarden.command.ReloadCommand;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class BaseCommand extends PrimaryCommand {

    public BaseCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("particles")
                .permission("roseparticles.basecommand")
                .aliases("rp", "roseparticles")
                .arguments(ArgumentsDefinition.builder()
                        .optionalSub(
                                new HelpCommand(this.rosePlugin, this),
                                new ReloadCommand(this.rosePlugin),
                                new SpawnCommand(this.rosePlugin)
                        ))
                .build();
    }

}
