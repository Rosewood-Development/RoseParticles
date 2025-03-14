package dev.rosewood.roseparticles.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseparticles.manager.ParticleManager;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.NamespacedKey;

public class ParticleFileArgumentHandler extends ArgumentHandler<ParticleFile> {

    public static final ArgumentHandler<ParticleFile> INSTANCE = new ParticleFileArgumentHandler();

    public ParticleFileArgumentHandler() {
        super(ParticleFile.class);
    }

    @Override
    public ParticleFile handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        ParticleManager particleManager = context.getRosePlugin().getManager(ParticleManager.class);
        ParticleFile particleFile = particleManager.getParticleFile(input.toLowerCase());
        if (particleFile == null)
            throw new HandledArgumentException("argument-handler-particle-file", StringPlaceholders.of("input", input));
        return particleFile;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] strings) {
        ParticleManager particleManager = context.getRosePlugin().getManager(ParticleManager.class);
        return new ArrayList<>(this.denamespaceify(particleManager.getParticleFiles().keySet()));
    }

    private Set<String> denamespaceify(Set<NamespacedKey> keys) {
        Set<String> uniques = new HashSet<>();
        Set<String> visited = new HashSet<>();
        for (NamespacedKey namespacedKey : keys) {
            String string = namespacedKey.toString();
            String key = namespacedKey.getKey();
            uniques.add(string);
            if (!visited.contains(key)) {
                uniques.add(key);
            } else {
                uniques.remove(key);
            }
            visited.add(string);
            visited.add(key);
        }
        return uniques;
    }

}
