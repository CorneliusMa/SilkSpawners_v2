package de.corneliusmay.silkspawners.plugin.platform;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class PlatformLoader {

    private final SilkSpawners plugin;

    private final ComponentLoader<ServerPlatform> loader =
            new ComponentLoader<>(ServerPlatform.class, "platform", JavaPlugin.class);

    @Getter
    private ServerPlatform serverPlatform;

    public PlatformLoader(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    public void load() {
        String platform = Server.isFolia() ? "folia" : "bukkit";
        this.serverPlatform = loader.instantiate(platform + ".PlatformImplementation", plugin);
        plugin.getLog().info("Initialized plugin for " + platform + " server");
    }
}
