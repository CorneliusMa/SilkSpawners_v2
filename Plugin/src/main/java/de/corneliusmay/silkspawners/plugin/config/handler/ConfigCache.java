package de.corneliusmay.silkspawners.plugin.config.handler;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;

import java.util.Map;

final class ConfigCache {

    private static volatile Map<PluginConfig, Object> values = Map.of();

    static Object value(PluginConfig key) {
        return values.get(key);
    }

    static void commit(Map<PluginConfig, Object> newValues) {
        values = Map.copyOf(newValues);
    }
}
