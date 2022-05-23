package de.corneliusmay.silkspawners.plugin.config.handler;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;

public class ConfigValue<T> {

    private final PluginConfig config;

    public ConfigValue(PluginConfig value) {
        this.config = value;
    }

    public T get() {
        String value = config.getConfig().getString(config.getPath());
        return (T) config.getFormatter().format(value);
    }
}
