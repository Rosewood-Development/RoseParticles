package dev.rosewood.roseparticles.config;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;
import dev.rosewood.roseparticles.RoseParticles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static dev.rosewood.rosegarden.config.RoseSettingSerializers.*;

public final class SettingKey {

    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();

    public static final RoseSetting<Long> UPDATE_FREQUENCY = create("update-frequency", LONG, 1L, "The number of ticks to wait between particle engine updates");
    public static final RoseSetting<CommentedConfigurationSection> RESOURCE_PACK_SERVER = create("resource-pack-server", "Resource pack server settings for hosting custom particle textures", "Can be disabled if you wish to include the particle textures in your own resource pack", "The resulting pack download URL will look like this: http://%external-hostname%:%port%/pack.zip");
    public static final RoseSetting<Boolean> RESOURCE_PACK_SERVER_ENABLED = create("resource-pack-server.enabled", BOOLEAN, true, "Should the resource pack server be enabled?", "Players will be automatically prompted to download the resource pack when joining");
    public static final RoseSetting<String> RESOURCE_PACK_SERVER_HOSTNAME = create("resource-pack-server.hostname", STRING, "0.0.0.0", "The hostname to bind to");
    public static final RoseSetting<Integer> RESOURCE_PACK_SERVER_PORT = create("resource-pack-server.port", INTEGER, 8080, "The port to bind to");
    public static final RoseSetting<String> RESOURCE_PACK_EXTERNAL_HOSTNAME = create("resource-pack-server.external-hostname", STRING, "", "The external hostname players use to connect to the server", "The same URL will be used to download the resource pack", "If blank, will use the hostname instead");

    private static <T> RoseSetting<T> create(String key, RoseSettingSerializer<T> serializer, T defaultValue, String... comments) {
        RoseSetting<T> setting = RoseSetting.backed(RoseParticles.getInstance(), key, serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static RoseSetting<CommentedConfigurationSection> create(String key, String... comments) {
        RoseSetting<CommentedConfigurationSection> setting = RoseSetting.backedSection(RoseParticles.getInstance(), key, comments);
        KEYS.add(setting);
        return setting;
    }

    public static List<RoseSetting<?>> getKeys() {
        return Collections.unmodifiableList(KEYS);
    }

    private SettingKey() {}

}
