package de.corneliusmay.silkspawners.plugin.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;

class MinecraftVersion {

    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+(\\.\\d+)+");

    @Getter
    private final String version;

    private final int majorVersion;

    private final int minorVersion;

    private final int patchVersion;

    private MinecraftVersion(String version) {
        this.version = version;
        String[] splitVersion = version.split("\\.");
        this.majorVersion = Integer.parseInt(splitVersion[0]);
        this.minorVersion = Integer.parseInt(splitVersion[1]);
        this.patchVersion = splitVersion.length > 2 ? Integer.parseInt(splitVersion[2]) : 0;
    }

    static MinecraftVersion parse(String bukkitVersion) {
        Matcher matcher = VERSION_PATTERN.matcher(bukkitVersion);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Unrecognized Minecraft version string: " + bukkitVersion);
        }
        return new MinecraftVersion(matcher.group());
    }

    /**
     * Checks if this version is newer or equal to the one provided.
     *
     * @param major the target major version
     * @param minor the target minor version. 0 for all
     * @param patch the target patch version. 0 for all
     * @return true if this version is newer or equal to the one provided
     */
    boolean isNewerOrEqualTo(int major, int minor, int patch) {
        if (majorVersion != major) return majorVersion > major;
        if (minorVersion != minor) return minorVersion > minor;
        return patchVersion >= patch;
    }
}
