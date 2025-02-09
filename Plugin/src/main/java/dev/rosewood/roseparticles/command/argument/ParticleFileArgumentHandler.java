package dev.rosewood.roseparticles.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseparticles.manager.ParticleManager;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import java.util.ArrayList;
import java.util.List;

public class ParticleFileArgumentHandler extends ArgumentHandler<ParticleFile> {

    public static final ArgumentHandler<ParticleFile> INSTANCE = new ParticleFileArgumentHandler();

    public ParticleFileArgumentHandler() {
        super(ParticleFile.class);
    }

    @Override
    public ParticleFile handle(CommandContext commandContext, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        ParticleManager particleManager = commandContext.getRosePlugin().getManager(ParticleManager.class);
        ParticleFile particleFile = particleManager.getParticleFiles().get(input.toLowerCase());
        if (particleFile == null)
            throw new HandledArgumentException("argument-handler-particle-file", StringPlaceholders.of("input", input));

        return particleFile;
    }

    @Override
    public List<String> suggest(CommandContext commandContext, Argument argument, String[] strings) {
        ParticleManager particleManager = commandContext.getRosePlugin().getManager(ParticleManager.class);
        return new ArrayList<>(particleManager.getParticleFiles().keySet());
    }

}
