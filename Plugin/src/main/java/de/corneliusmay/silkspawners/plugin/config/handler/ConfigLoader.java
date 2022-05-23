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
    }
}
