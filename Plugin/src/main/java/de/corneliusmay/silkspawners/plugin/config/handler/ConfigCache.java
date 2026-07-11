package de.corneliusmay.silkspawners.plugin.config.handler;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class ConfigCache {

    private static final Map<PluginConfig, Object> VALUES = new ConcurrentHashMap<>();

    private ConfigCache() {
    }

    static Object value(PluginConfig key) {
        return VALUES.get(key);
    }

    static void commit(Map<PluginConfig, Object> values) {
        VALUES.clear();
        VALUES.putAll(values);
    }
}
