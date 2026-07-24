package de.corneliusmay.silkspawners.plugin.version;

import static de.corneliusmay.silkspawners.plugin.version.MinecraftVersionChecker.getBukkitVersion;

import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Provides;
import de.corneliusmay.silkspawners.wiring.Wired;

@Wired
public class CrossVersionHandler implements Loader {

    private final ComponentLoader<VersionAdapter> loader = new ComponentLoader<>(VersionAdapter.class, "bukkit");

    private VersionAdapter versionAdapter;

    @Provides
    public VersionAdapter getVersionAdapter() {
        return versionAdapter;
    }

    private boolean fail(String message) {
        Logger.error(message);
        Logger.warn("Disabling plugin due to version incompatibility");
        return false;
    }

    @Override
    public boolean load() {
        Logger.info("Loading Cross-Version support");
        MinecraftVersion version;
        try {
            version = MinecraftVersion.parse(org.bukkit.Bukkit.getBukkitVersion());
        } catch (IllegalArgumentException ex) {
            return fail("Could not detect the server version (" + org.bukkit.Bukkit.getBukkitVersion() + ")");
        }

        String bukkitVersion = getBukkitVersion(version);
        if (bukkitVersion == null) {
            return fail("The detected Server Version (" + version.getVersion()
                    + ") is too old for the currently installed version of SilkSpawners");
        }

        this.versionAdapter = loader.instantiate(bukkitVersion + ".VersionImplementation");
        Logger.info("Loaded support for version " + version.getVersion());
        return true;
    }
}
