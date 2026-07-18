package de.corneliusmay.silkspawners.plugin.platform;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Provides;
import de.corneliusmay.silkspawners.wiring.Wired;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@Wired
@RequiredArgsConstructor
public class PlatformLoader implements Loader {

    private final SilkSpawners plugin;

    private final ComponentLoader<ServerPlatform> loader =
            new ComponentLoader<>(ServerPlatform.class, "platform", JavaPlugin.class);

    private ServerPlatform serverPlatform;

    @Provides
    public ServerPlatform getServerPlatform() {
        return serverPlatform;
    }

    @Override
    public boolean load() {
        Logger.info("Loading server platform");
        String platform = Server.isFolia() ? "folia" : "bukkit";
        this.serverPlatform = loader.instantiate(platform + ".PlatformImplementation", plugin);
        Logger.info("Initialized plugin for " + platform + " server");
        return true;
    }
}
