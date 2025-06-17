package dev.rosewood.roseparticles.nms;

import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Bukkit;

public final class NMSAdapter {

    private static NMSHandler nmsHandler;

    private NMSAdapter() {

    }

    static {
        try {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            if (name.contains("R")) { // Contains NMS package version, use that
                name = name.substring(name.lastIndexOf('.') + 1);
            } else { // We started not having these identifiers with Paper starting 1.20.5
                int major = NMSUtil.getVersionNumber();
                int minor = NMSUtil.getMinorVersionNumber();
                if (major == 21 && minor == 4) {
                    name = "v1_21_R3";
                } else if (major == 21 && minor == 5) {
                    name = "v1_21_R4";
                }
            }

            nmsHandler = (NMSHandler) Class.forName("dev.rosewood.roseparticles.nms." + name + ".NMSHandlerImpl").getConstructor().newInstance();
        } catch (Exception ignored) { }
    }

    /**
     * @return true if this server version is supported, false otherwise
     */
    public static boolean isValidVersion() {
        return nmsHandler != null;
    }

    /**
     * @return the instance of the NMSHandler, or null if this server version is not supported
     */
    public static NMSHandler getHandler() {
        return nmsHandler;
    }

}
