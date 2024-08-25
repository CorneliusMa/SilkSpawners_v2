package de.corneliusmay.silkspawners.plugin.platform;

import de.corneliusmay.silkspawners.api.ServerPlatform;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class PlatformLoader {

    private final SilkSpawners plugin;

    @Getter
    private ServerPlatform serverPlatform;

    public PlatformLoader(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    private Class<?> loadClass(String platformName) throws ClassNotFoundException {
        return Class.forName("de.corneliusmay.silkspawners.platform." + platformName + ".PlatformImplementation");
    }

    public void load() {
        String platform = Server.isFolia() ? "folia" : "bukkit";
        try {
            Class<?> clazz = loadClass(platform);
            this.serverPlatform = (ServerPlatform) clazz.getConstructor(JavaPlugin.class).newInstance(this.plugin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        plugin.getLog().info("Initialized plugin for " + platform + " server");
    }
}
