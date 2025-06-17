package dev.rosewood.roseparticles.config;

import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.SettingHolder;
import dev.rosewood.rosegarden.config.SettingSerializer;
import dev.rosewood.roseparticles.RoseParticles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import static dev.rosewood.rosegarden.config.SettingSerializers.*;

public final class SettingKey implements SettingHolder {

    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();
    public static final SettingKey INSTANCE = new SettingKey();

    public static final RoseSetting<Long> UPDATE_FREQUENCY = create("update-frequency", LONG, 1L, "The number of ticks to wait between particle engine updates", "Values higher than 3 can cause particle collisions to act weird");
    public static final RoseSetting<Double> RENDER_DISTANCE = create("render-distance", DOUBLE, 64.0, "The max distance in blocks away to render particles for the player");
    public static final RoseSetting<Boolean> RUN_ASYNC = create("run-async", BOOLEAN, false, "When enabled, all particle engine calculations and packets are handled off the main thread", "May cause animations to become slightly desynced with the server tick at the cost of better performance");
    public static final RoseSetting<ConfigurationSection> RESOURCE_PACK_SERVER = create("resource-pack-server", "Resource pack server settings for hosting custom particle textures", "Can be disabled if you wish to include the particle textures in your own resource pack", "The resulting pack download URL will look like this: http://%external-hostname%:%port%/pack.zip");
    public static final RoseSetting<Boolean> RESOURCE_PACK_SERVER_ENABLED = create("resource-pack-server.enabled", BOOLEAN, true, "Should the resource pack server be enabled?", "Players will be automatically prompted to download the resource pack when joining");
    public static final RoseSetting<String> RESOURCE_PACK_SERVER_HOSTNAME = create("resource-pack-server.hostname", STRING, "0.0.0.0", "The hostname to bind to");
    public static final RoseSetting<Integer> RESOURCE_PACK_SERVER_PORT = create("resource-pack-server.port", INTEGER, 8080, "The port to bind to");
    public static final RoseSetting<String> RESOURCE_PACK_EXTERNAL_HOSTNAME = create("resource-pack-server.external-hostname", STRING, "", "The external hostname players use to connect to the server", "The same URL will be used to download the resource pack", "If blank, will use the hostname instead");

    private static <T> RoseSetting<T> create(String key, SettingSerializer<T> serializer, T defaultValue, String... comments) {
        RoseSetting<T> setting = RoseSetting.ofBackedValue(key, RoseParticles.getInstance(), serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static RoseSetting<ConfigurationSection> create(String key, String... comments) {
        RoseSetting<ConfigurationSection> setting = RoseSetting.ofBackedSection(key, RoseParticles.getInstance(), comments);
        KEYS.add(setting);
        return setting;
    }

    public static List<RoseSetting<?>> getKeys() {
        return Collections.unmodifiableList(KEYS);
    }

    private SettingKey() {}

    @Override
    public List<RoseSetting<?>> get() {
        return KEYS;
    }

}
