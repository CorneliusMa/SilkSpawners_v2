package de.corneliusmay.silkspawners.plugin.config.handler;

import static de.corneliusmay.silkspawners.plugin.config.PluginConfig.CONFIG_VERSION;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigLoader {

    private final Plugin plugin;

    @Getter
    private boolean loaded;

    public ConfigLoader(Plugin plugin) {
        this.plugin = plugin;
        this.loaded = false;
        this.load();
    }

    private int getConfigVersion(FileConfiguration config) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            log(
                    Level.INFO,
                    "No config file was found. The config will be generated with the default" + " configuration");
            return CONFIG_VERSION;
        }

        int currentVersion = config.getInt("update.configVersion");
        if (currentVersion == 0) currentVersion = 1;
        if (CONFIG_VERSION > currentVersion) {
            config.set("update.configVersion", CONFIG_VERSION);
            log(Level.WARNING, conversionMessage(currentVersion));
        } else {
            log(Level.INFO, "Configuration is up to date");
        }

        return currentVersion;
    }

    private void load() {
        log(Level.INFO, "Loading configuration...");
        loaded = apply(true);
    }

    public boolean reload() {
        log(Level.INFO, "Reloading configuration...");
        plugin.reloadConfig();
        return apply(false);
    }

    private boolean apply(boolean initialLoad) {
        FileConfiguration config = plugin.getConfig();

        int configVersion = getConfigVersion(config);
        for (PluginConfig value : PluginConfig.values()) {
            value.init(config, configVersion);
        }
        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();

        Map<PluginConfig, Object> values = new HashMap<>();

        boolean valid = true;
        for (PluginConfig value : PluginConfig.values()) {
            try {
                values.put(value, new ConfigValue<>(value).load());
            } catch (Exception ex) {
                plugin.getLogger().severe(invalidValueMessage(value, config, ex));
                valid = false;
            }
        }

        if (valid) ConfigCache.commit(values);
        else if (initialLoad) {
            plugin.getLogger().severe("Disabling plugin due to invalid configuration value");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
        return valid;
    }

    private void log(Level level, String message) {
        Bukkit.getLogger().log(level, "[SilkSpawners] " + message);
    }

    private String conversionMessage(int currentVersion) {
        return "Configuration file in version "
                + currentVersion
                + " is automatically converted to the latest version "
                + CONFIG_VERSION;
    }

    private String invalidValueMessage(PluginConfig value, FileConfiguration config, Exception ex) {
        return "Invalid configuration value: "
                + value.getPath()
                + ": "
                + config.getString(value.getPath())
                + " ("
                + ex.getMessage()
                + ")";
    }
}
