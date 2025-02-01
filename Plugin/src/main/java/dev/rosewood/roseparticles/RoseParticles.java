package dev.rosewood.roseparticles;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseparticles.config.SettingKey;
import dev.rosewood.roseparticles.manager.CommandManager;
import dev.rosewood.roseparticles.manager.HologramManager;
import dev.rosewood.roseparticles.manager.LocaleManager;
import dev.rosewood.roseparticles.manager.ParticleManager;
import java.util.List;

/**
 * @author Esophose
 */
public class RoseParticles extends RosePlugin {

    /**
     * The running instance of RoseLoot on the server
     */
    private static RoseParticles instance;

    public static RoseParticles getInstance() {
        return instance;
    }

    public RoseParticles() {
        super(-1, 24283, null, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    @Override
    public void enable() {
        if (NMSUtil.getVersionNumber() < 19 || (NMSUtil.getVersionNumber() == 19 && NMSUtil.getMinorVersionNumber() != 4))
            this.getLogger().severe("This server version does not support display entities. The plugin will not work properly.");
    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                HologramManager.class,
                ParticleManager.class
        );
    }

    @Override
    public boolean isLocalDatabaseOnly() {
        return true;
    }

    @Override
    protected List<RoseSetting<?>> getRoseConfigSettings() {
        return SettingKey.getKeys();
    }

    @Override
    protected String[] getRoseConfigHeader() {
        return """
                __________                   __________                __   __        __
                \\______   \\ ____  ______ ____\\______   \\_____ ________/  |_|__| ____ |  |   ____   ______
                 |       _//  _ \\/  ___// __ \\|     ___/\\__  \\\\_  __ \\   __\\  |/ ___\\|  | _/ __ \\ /  ___/
                 |    |   (  <_> )___ \\\\  ___/|    |     / __ \\|  | \\/|  | |  \\  \\___|  |_\\  ___/ \\___
                 |____|_  /\\____/____  >\\___  >____|    (____  /__|   |__| |__|\\___  >____/\\___  >____  >
                        \\/           \\/     \\/               \\/                    \\/          \\/     \\/
               """.lines().toArray(String[]::new);
    }

}
