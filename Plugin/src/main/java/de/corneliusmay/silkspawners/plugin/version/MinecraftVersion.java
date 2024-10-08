package de.corneliusmay.silkspawners.plugin.version;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.logging.Level;

class MinecraftVersion {

    @Getter
    private static String version;

    @Getter
    private static int majorVersion, minorVersion, patchVersion;

    static {
        try {
            // Split on the "-" to just get the version information
            version = Bukkit.getServer().getBukkitVersion().split("-")[0];
            final String[] splitVersion = version.split("\\.");

            majorVersion = Integer.parseInt(splitVersion[0]);
            minorVersion = Integer.parseInt(splitVersion[1]);
            if (splitVersion.length > 2) {
                patchVersion = Integer.parseInt(splitVersion[2]);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[SilkSpawners] Failed to parse server version!", e);
        }
    }

    /**
     * Checks if the current server version is newer or equal to the one provided.
     *
     * @param major the target major version
     * @param minor the target minor version. 0 for all
     * @param patch the target patch version. 0 for all
     * @return true if the server version is newer or equal to the one provided
     */
    static boolean versionIsNewerOrEqualTo(int major, int minor, int patch) {
        if (getMajorVersion() > major) {
            return true;
        } else if (getMajorVersion() == major) {
            if (getMinorVersion() > minor) {
                return true;
            } else if (getMinorVersion() == minor) {
                return getPatchVersion() >= patch;
            }
        }
        return false;
    }
}
