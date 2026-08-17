package de.corneliusmay.silkspawners.plugin.version;

import de.corneliusmay.silkspawners.plugin.utils.Logger;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Provides;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
@Singleton
class MinecraftVersionLoader implements Loader {

    private final Logger logger;

    private MinecraftVersion version;

    @Provides
    public MinecraftVersion getVersion() {
        return version;
    }

    @Override
    public boolean load() {
        try {
            version = MinecraftVersion.parse(Bukkit.getBukkitVersion());
            return true;
        } catch (IllegalArgumentException ex) {
            logger.error("Could not detect the server version (" + Bukkit.getBukkitVersion() + ")");
            logger.warn("Disabling plugin due to version incompatibility");
            return false;
        }
    }
}
