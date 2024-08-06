package de.corneliusmay.silkspawners.plugin.version;

import de.corneliusmay.silkspawners.api.NMS;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;

public class MinecraftVersionChecker {

    private final SilkSpawners plugin;

    @Getter
    private NMS nmsHandler;

    public MinecraftVersionChecker(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    public boolean load() {
        String version = getServerVersion();

        if (version == null) {
            plugin.getLog().error("The detected Server Version (" + MinecraftVersion.getVersion() + ") is not supported by the currently installed version of SilkSpawners");
            plugin.getLog().info("You can check for updates at https://www.spigotmc.org/resources/silkspawners-versions-1-8-8-1-18-2.60063/");
            plugin.getLog().warn("Disabling plugin due to version incompatibility");
            plugin.getPluginLoader().disablePlugin(plugin);
            return false;
        }

        try {
            Class<?> clazz = Class.forName("de.corneliusmay.silkspawners.nms." + version + ".NMSHandler");
            this.nmsHandler = (NMS) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        plugin.getLog().info("Loading support for version " + MinecraftVersion.getVersion());
        return true;
    }

    private String getServerVersion() {
        // As of Minecraft version 1.20.5, Paper ships with a Mojang-mapped runtime instead of reobfuscating the server
        // to Spigot mappings. This means that the package name of the server implementation is no longer a reliable
        // way to determine the server version. Instead, we can use the Bukkit version string.
        //
        // The following code also means that we don't have to update the plugin for every new Minecraft version
        // unless the Bukkit API changes in a way that explicitly breaks it.
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 20, 5)) {
            return "v1_20_R4";
        }
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 13, 1)) {
            return "v1_13_R2";
        }
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 12, 0)) {
            return "v1_12_R1";
        }
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 9, 4)) {
            return "v1_9_R2";
        }
        if(MinecraftVersion.versionIsNewerOrEqualTo(1, 8, 4)) {
            return "v1_8_R3";
        }
        return null;
    }
}
