package de.corneliusmay.silkspawners.plugin.config.handler;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;

import java.util.List;

public class ConfigValueArray<T> {

    private final PluginConfig config;

    public ConfigValueArray(PluginConfig value) {
        this.config = value;
    }

    public List<T> get() {
        List<String> value = config.getConfig().getStringList(config.getPath());
        return (List<T>) value.stream().map((s) -> config.getFormatter().format(s)).toList();
    }
}
