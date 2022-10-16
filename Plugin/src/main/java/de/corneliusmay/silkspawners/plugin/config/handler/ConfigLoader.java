package de.corneliusmay.silkspawners.plugin.config.handler;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigLoader {

    private final Plugin plugin;

    private final FileConfiguration config;

    public ConfigLoader(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void load() {
        for(PluginConfig value : PluginConfig.values()) value.init(config);
        config.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();

        for(PluginConfig value : PluginConfig.values()) {
            try {
                new ConfigValue<>(value).get();
            } catch (Exception ex) {
                plugin.getLogger().severe("Disabling plugin due to invalid configuration value: " + value.getPath() + ": " + config.getString(value.getPath()));
                plugin.getPluginLoader().disablePlugin(plugin);
            }
        }
    }
}
