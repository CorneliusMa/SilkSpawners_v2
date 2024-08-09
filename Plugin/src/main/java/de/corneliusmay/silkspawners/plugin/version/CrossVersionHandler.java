package de.corneliusmay.silkspawners.plugin.version;

import de.corneliusmay.silkspawners.api.NMS;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;

import static de.corneliusmay.silkspawners.plugin.version.MinecraftVersionChecker.getServerVersion;

public class CrossVersionHandler {

    private final SilkSpawners plugin;

    @Getter
    private NMS nmsHandler;

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
        String version = getServerVersion();

        if (version == null) {
            return disablePlugin("The detected Server Version (" + MinecraftVersion.getVersion() + ") is too old for the currently installed version of SilkSpawners");
        }

        try {
            Class<?> clazz = loadClass("nms." + version + ".NMSHandler");
            this.nmsHandler = (NMS) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        plugin.getLog().info("Loading support for version " + MinecraftVersion.getVersion());
        return true;
    }
}
