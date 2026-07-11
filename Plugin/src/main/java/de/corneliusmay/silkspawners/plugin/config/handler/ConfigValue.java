package de.corneliusmay.silkspawners.plugin.config.handler;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;

public class ConfigValue<T> {

    private final PluginConfig config;

    public ConfigValue(PluginConfig value) {
        this.config = value;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) ConfigCache.value(config);
    }

    @SuppressWarnings("unchecked")
    T load() {
        return (T) config.getFormatter().format(config.getConfig().getString(config.getPath()));
    }
}
