package dev.rosewood.roseparticles.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Entity;

public class RelativeLocationArgumentHandler extends ArgumentHandler<Location> {

    public static final ArgumentHandler<Location> INSTANCE = new RelativeLocationArgumentHandler();

    private final DecimalFormat decimalFormat;

    public RelativeLocationArgumentHandler() {
        super(Location.class);
        this.decimalFormat = new DecimalFormat("0.##");
    }

    @Override
    public Location handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        World world = context.get(World.class);
        Location origin = null;

        if (context.getSender() instanceof Entity entity) {
            origin = entity.getLocation();
        } else if (context.getSender() instanceof BlockCommandSender block) {
            origin = block.getBlock().getLocation();
        }

        if (world == null && origin != null)
            world = origin.getWorld();

        if (world == null)
            throw new HandledArgumentException("argument-handler-relative-location-missing-world");

        String xInput = inputIterator.next();
        String yInput = inputIterator.next();
        String zInput = inputIterator.next();

        boolean relativeX = xInput.startsWith("~");
        if (relativeX)
            xInput = xInput.substring(1);

        boolean relativeY = yInput.startsWith("~");
        if (relativeY)
            yInput = yInput.substring(1);

        boolean relativeZ = zInput.startsWith("~");
        if (relativeZ)
            zInput = zInput.substring(1);

        if (origin == null && (relativeX || relativeY || relativeZ))
            throw new HandledArgumentException("argument-handler-relative-location-console");

        try {
            double x = xInput.isEmpty() ? 0 : Double.parseDouble(xInput);
            double y = yInput.isEmpty() ? 0 : Double.parseDouble(yInput);
            double z = zInput.isEmpty() ? 0 : Double.parseDouble(zInput);

            if (relativeX)
                x += origin.getX();

            if (relativeY)
                y += origin.getY();

            if (relativeZ)
                z += origin.getZ();

            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            throw new HandledArgumentException("argument-handler-relative-location", StringPlaceholders.of("x", xInput, "y", yInput, "z", zInput));
        }
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] strings) {
        List<String> suggestions = new ArrayList<>();

        Location origin = null;
        if (context.getSender() instanceof Entity entity) {
            origin = entity.getLocation();
        } else if (context.getSender() instanceof BlockCommandSender block) {
            origin = block.getBlock().getLocation();
        }

        if (origin != null) {
            suggestions.add(String.format("%s %s %s", this.decimalFormat.format(origin.getX()), this.decimalFormat.format(origin.getY()), this.decimalFormat.format(origin.getZ())));
            suggestions.add("~ ~ ~");
        } else {
            suggestions.add("0 0 0");
        }

        return suggestions;
    }

}
