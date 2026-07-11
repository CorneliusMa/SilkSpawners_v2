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

    Object load() {
        if (config.isList()) {
            return config.getConfig().getStringList(config.getPath()).stream()
                    .map(s -> config.getFormatter().format(s))
                    .toList();
        }
        return config.getFormatter().format(config.getConfig().getString(config.getPath()));
    }
}
