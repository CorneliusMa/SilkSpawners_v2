package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

final class PluginConfigBuilder {

    final ConfigScope scope;
    final String key;
    ConfigValueFormatter<?> formatter;
    Object defaultValue;
    String[] legacyKeys;
    boolean list;
    final NavigableMap<Integer, List<ConfigValueMigrator>> migrators = new TreeMap<>();

    PluginConfigBuilder(ConfigScope scope, String key) {
        this.scope = scope;
        this.key = key;
    }

    PluginConfigBuilder formatter(ConfigValueFormatter<?> formatter) {
        this.formatter = formatter;
        return this;
    }

    PluginConfigBuilder def(Object value) {
        this.defaultValue = value;
        return this;
    }

    PluginConfigBuilder legacy(String... legacyKeys) {
        this.legacyKeys = legacyKeys;
        return this;
    }

    PluginConfigBuilder list() {
        this.list = true;
        return this;
    }

    PluginConfigBuilder migrator(Integer configVersion, ConfigValueMigrator migrator) {
        this.migrators.computeIfAbsent(configVersion, version -> new ArrayList<>()).add(migrator);
        return this;
    }
}
