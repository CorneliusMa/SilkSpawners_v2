package de.corneliusmay.silkspawners.plugin.config.handler;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Level;

import static de.corneliusmay.silkspawners.plugin.config.PluginConfig.CONFIG_VERSION;

public class ConfigLoader {

    private final Plugin plugin;

    private final FileConfiguration config;

    public ConfigLoader(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    private int getConfigVersion() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            Bukkit.getLogger().log(Level.INFO, "[SilkSpawners] No config file was found. The config will be generated with the default configuration");
            return CONFIG_VERSION;
        }

        int currentVersion = config.getInt("update.configVersion");
        if (currentVersion == 0) currentVersion = 1;
        if (CONFIG_VERSION > currentVersion) {
            config.set("update.configVersion", CONFIG_VERSION);
            Bukkit.getLogger().log(Level.WARNING, "[SilkSpawners] Configuration file in version " + currentVersion + " is automatically converted to the latest version " + CONFIG_VERSION);
        } else {
            Bukkit.getLogger().log(Level.INFO, "[SilkSpawners] Configuration is up to date");
        }

        return currentVersion;
    }

    public void load() {
        Bukkit.getLogger().log(Level.INFO, "[SilkSpawners] Loading configuration...");

        int configVersion = getConfigVersion();
        for (PluginConfig value : PluginConfig.values()) {
            value.init(config, configVersion);
        }
        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();

        for (PluginConfig value : PluginConfig.values()) {
            try {
                new ConfigValue<>(value).get();
            } catch (Exception ex) {
                plugin.getLogger().severe("Disabling plugin due to invalid configuration value: " + value.getPath() + ": " + config.getString(value.getPath()));
                plugin.getPluginLoader().disablePlugin(plugin);
            }
        }
    }
}
