package de.corneliusmay.silkspawners.plugin.version;

import static de.corneliusmay.silkspawners.plugin.version.MinecraftVersionChecker.getBukkitVersion;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.plugin.loader.Loader;
import de.corneliusmay.silkspawners.spi.version.Bukkit;
import lombok.Getter;

public class CrossVersionHandler implements Loader {

    private final SilkSpawners plugin;

    private final ComponentLoader<Bukkit> loader = new ComponentLoader<>(Bukkit.class, "bukkit");

    @Getter
    private Bukkit bukkitHandler;

    public CrossVersionHandler(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    private boolean fail(String message) {
        plugin.getLog().error(message);
        plugin.getLog().warn("Disabling plugin due to version incompatibility");
        return false;
    }

    @Override
    public boolean load() {
        plugin.getLog().info("Loading Cross-Version support");
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

        this.bukkitHandler = loader.instantiate(bukkitVersion + ".BukkitHandler");
        plugin.getLog().info("Loaded support for version " + version.getVersion());
        return true;
    }
}
