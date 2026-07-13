package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

final class ConfigKeyBuilder {

    final ConfigScope scope;
    final String key;
    ConfigValueFormatter<?> formatter;
    Object defaultValue;
    String[] legacyKeys;
    boolean list;
    final NavigableMap<Integer, List<ConfigValueMigrator>> migrators = new TreeMap<>();

    ConfigKeyBuilder(ConfigScope scope, String key) {
        this.scope = scope;
        this.key = key;
    }

    ConfigKeyBuilder def(Object value) {
        this.defaultValue = value;
        return this;
    }

    ConfigKeyBuilder legacy(String... legacyKeys) {
        this.legacyKeys = legacyKeys;
        return this;
    }

    ConfigKeyBuilder migrator(Integer configVersion, ConfigValueMigrator migrator) {
        this.migrators
                .computeIfAbsent(configVersion, version -> new ArrayList<>())
                .add(migrator);
        return this;
    }

    <T> ConfigKey<T> formatter(ConfigValueFormatter<T> formatter) {
        this.formatter = formatter;
        return new ConfigKey<>(this);
    }

    <T> ConfigKey<List<T>> listFormatter(ConfigValueFormatter<T> formatter) {
        this.formatter = formatter;
        this.list = true;
        return new ConfigKey<>(this);
    }
}
