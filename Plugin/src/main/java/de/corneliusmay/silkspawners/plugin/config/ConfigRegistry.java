package de.corneliusmay.silkspawners.plugin.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
final class ConfigRegistry {

    private final List<ConfigKey<?>> keys = new ArrayList<>();

    private volatile Map<ConfigKey<?>, Object> values = Map.of();

    void register(ConfigKey<?> key) {
        keys.add(key);
    }

    List<ConfigKey<?>> keys() {
        return List.copyOf(keys);
    }

    Object value(ConfigKey<?> key) {
        Object value = values.get(key);
        return value != null ? value : key.formatDefault();
    }

    void commit(Map<ConfigKey<?>, Object> newValues) {
        values = Map.copyOf(newValues);
    }

    void update(ConfigKey<?> key, Object value) {
        Map<ConfigKey<?>, Object> updated = new HashMap<>(values);
        updated.put(key, value);
        values = Map.copyOf(updated);
    }
}
