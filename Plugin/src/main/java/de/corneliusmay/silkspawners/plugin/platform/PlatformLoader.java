package de.corneliusmay.silkspawners.plugin.platform;

import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Provides;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor
public class PlatformLoader implements Loader, Dumpable {

    private final JavaPlugin plugin;

    private final ComponentLoader<ServerPlatform> loader =
            new ComponentLoader<>(ServerPlatform.class, "platform", JavaPlugin.class);

    private final Logger logger;

    private ServerPlatform serverPlatform;

    @Provides
    public ServerPlatform getServerPlatform() {
        return serverPlatform;
    }

    @Override
    public void describe(DumpObject<?> writer) {
        writer.section("platform")
                .value("implementation", serverPlatform.getClass().getName());
    }

    @Override
    public boolean load() {
        logger.info("Loading server platform");
        String platform = Server.isFolia() ? "folia" : "bukkit";
        this.serverPlatform = loader.instantiate(platform + ".PlatformImplementation", plugin);
        logger.info("Initialized plugin for " + platform + " server");
        return true;
    }
}
