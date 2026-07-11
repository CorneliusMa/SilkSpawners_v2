package de.corneliusmay.silkspawners.plugin.config.handler;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;

import java.util.List;

public class ConfigValueArray<T> {

    private final PluginConfig config;

    public ConfigValueArray(PluginConfig value) {
        this.config = value;
    }

    @SuppressWarnings("unchecked")
    public List<T> get() {
        return (List<T>) ConfigCache.array(config);
    }

    @SuppressWarnings("unchecked")
    List<T> load() {
        return (List<T>) config.getConfig().getStringList(config.getPath()).stream()
                .map(s -> config.getFormatter().format(s))
                .toList();
    }
}
