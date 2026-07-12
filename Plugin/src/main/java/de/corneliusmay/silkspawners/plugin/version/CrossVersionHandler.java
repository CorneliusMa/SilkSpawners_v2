package de.corneliusmay.silkspawners.plugin.version;

import de.corneliusmay.silkspawners.spi.version.Bukkit;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import lombok.Getter;

import static de.corneliusmay.silkspawners.plugin.version.MinecraftVersionChecker.getBukkitVersion;

public class CrossVersionHandler {

    private final SilkSpawners plugin;

    private final ComponentLoader<Bukkit> loader = new ComponentLoader<>(Bukkit.class, "bukkit");

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

    public boolean load() {
        String bukkitVersion = getBukkitVersion();
        if (bukkitVersion == null) {
            return disablePlugin("The detected Server Version (" + MinecraftVersion.getVersion() + ") is too old for the currently installed version of SilkSpawners");
        }

        this.bukkitHandler = loader.instantiate(bukkitVersion + ".BukkitHandler");
        plugin.getLog().info("Loaded support for version " + MinecraftVersion.getVersion());
        return true;
    }
}
