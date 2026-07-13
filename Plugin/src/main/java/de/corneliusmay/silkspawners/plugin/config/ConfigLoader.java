package de.corneliusmay.silkspawners.plugin.config;

import static de.corneliusmay.silkspawners.plugin.config.PluginConfig.CONFIG_VERSION;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigLoader {

    private final Plugin plugin;

    public ConfigLoader(Plugin plugin) {
        this.plugin = plugin;
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

    public boolean load() {
        log(Level.INFO, "Loading configuration...");
        return apply(true);
    }

    public boolean reload() {
        log(Level.INFO, "Reloading configuration...");
        plugin.reloadConfig();
        return apply(false);
    }

    private boolean apply(boolean initialLoad) {
        FileConfiguration config = plugin.getConfig();

        int configVersion = getConfigVersion(config);
        for (ConfigKey<?> key : PluginConfig.values()) {
            init(key, config, configVersion);
        }
        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();

        Map<ConfigKey<?>, Object> values = new HashMap<>();

        boolean valid = true;
        for (ConfigKey<?> key : PluginConfig.values()) {
            try {
                values.put(key, load(key, config));
            } catch (Exception ex) {
                plugin.getLogger().severe(invalidValueMessage(key, config, ex));
                valid = false;
            }
        }

        if (valid) ConfigRegistry.commit(values);
        else if (initialLoad) {
            plugin.getLogger().severe("Disabling plugin due to invalid configuration value");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
        return valid;
    }

    private void init(ConfigKey<?> key, FileConfiguration config, int initialVersion) {
        migrateLegacyKey(key, config, initialVersion);
        migrateValue(key, config, initialVersion);
        config.addDefault(key.getPath(), key.getDefaultValue());
    }

    private void migrateLegacyKey(ConfigKey<?> key, FileConfiguration config, int initialVersion) {
        String[] legacyKeys = key.getLegacyKeys();
        if (legacyKeys == null || legacyKeys.length == 0 || initialVersion == CONFIG_VERSION) return;

        int legacyKeyIndex = legacyKeys.length >= initialVersion ? initialVersion : legacyKeys.length;
        String legacyKey = legacyKeys[legacyKeyIndex - 1];
        Object legacyValue = config.get(legacyKey);
        if (legacyValue != null) {
            if (config.get(key.getPath(), null) == null) config.set(key.getPath(), legacyValue);
            config.set(legacyKey, null);
        }
    }

    private void migrateValue(ConfigKey<?> key, FileConfiguration config, int initialVersion) {
        if (initialVersion >= CONFIG_VERSION || key.getMigrators().isEmpty()) return;

        Object value = config.get(key.getPath(), null);
        boolean migrated = false;
        for (List<ConfigValueMigrator> versionMigrators :
                key.getMigrators().tailMap(initialVersion, false).values()) {
            for (ConfigValueMigrator migrator : versionMigrators) {
                Object result = migrator.migrate(value);
                if (result != null) {
                    value = result;
                    migrated = true;
                }
            }
        }
        if (migrated) config.set(key.getPath(), value);
    }

    private Object load(ConfigKey<?> key, FileConfiguration config) {
        if (key.isList()) {
            return config.getStringList(key.getPath()).stream()
                    .map(s -> key.getFormatter().format(s))
                    .toList();
        }
        return key.getFormatter().format(config.get(key.getPath()));
    }

    private void log(Level level, String message) {
        plugin.getLogger().log(level, message);
    }

    private String conversionMessage(int currentVersion) {
        return "Configuration file in version "
                + currentVersion
                + " is automatically converted to the latest version "
                + CONFIG_VERSION;
    }

    private String invalidValueMessage(ConfigKey<?> key, FileConfiguration config, Exception ex) {
        return "Invalid configuration value: "
                + key.getPath()
                + ": "
                + config.getString(key.getPath())
                + " ("
                + ex.getMessage()
                + ")";
    }
}
