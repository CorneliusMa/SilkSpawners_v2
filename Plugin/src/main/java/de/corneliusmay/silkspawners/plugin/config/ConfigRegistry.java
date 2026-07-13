package de.corneliusmay.silkspawners.plugin.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class ConfigRegistry {

    private static final List<ConfigKey<?>> KEYS = new ArrayList<>();

    private static volatile Map<ConfigKey<?>, Object> values = Map.of();

    private ConfigRegistry() {}

    static void register(ConfigKey<?> key) {
        KEYS.add(key);
    }

    static List<ConfigKey<?>> keys() {
        return List.copyOf(KEYS);
    }

    static Object value(ConfigKey<?> key) {
        return values.get(key);
    }

    static void commit(Map<ConfigKey<?>, Object> newValues) {
        values = Map.copyOf(newValues);
    }
}
