package de.corneliusmay.silkspawners.plugin.version;

import de.corneliusmay.silkspawners.api.Bukkit;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;

import static de.corneliusmay.silkspawners.plugin.version.MinecraftVersionChecker.getBukkitVersion;

public class CrossVersionHandler {

    private final SilkSpawners plugin;

    @Getter
    private Bukkit bukkitHandler;

    public CrossVersionHandler(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    private boolean disablePlugin(String message) {
        plugin.getLog().error(message);
        plugin.getLog().warn("Disabling plugin due to version incompatibility");
        plugin.getPluginLoader().disablePlugin(plugin);
        return false;
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        return Class.forName("de.corneliusmay.silkspawners." + className);
    }

    public boolean load() {
        String bukkitVersion = getBukkitVersion();
        if (bukkitVersion == null) {
            return disablePlugin("The detected Server Version (" + MinecraftVersion.getVersion() + ") is too old for the currently installed version of SilkSpawners");
        }

        try {
            Class<?> clazz = loadClass("bukkit." + bukkitVersion + ".BukkitHandler");
            this.bukkitHandler = (Bukkit) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        plugin.getLog().info("Loaded support for version " + MinecraftVersion.getVersion());
        return true;
    }
}
