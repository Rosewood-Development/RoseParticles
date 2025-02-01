package dev.rosewood.roseparticles.nms.hologram;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;

public final class HologramProperty<T> {

    public static final HologramProperty<Integer> INTERPOLATION_DELAY = new HologramProperty<>();
    public static final HologramProperty<Integer> TRANSFORMATION_DELAY = new HologramProperty<>();
    public static final HologramProperty<Integer> POSITION_ROTATION_DELAY = new HologramProperty<>();
    public static final HologramProperty<Display.Brightness> BRIGHTNESS = new HologramProperty<>();
    public static final HologramProperty<String> TEXT_JSON = new HologramProperty<>();
    public static final HologramProperty<Display.Billboard> BILLBOARD = new HologramProperty<>();
    public static final HologramProperty<Location> LOCATION = new HologramProperty<>();
    public static final HologramProperty<Transformation> TRANSFORMATION = new HologramProperty<>();
    public static final HologramProperty<Color> BACKGROUND_COLOR = new HologramProperty<>();

    private HologramProperty() {

    }

}
