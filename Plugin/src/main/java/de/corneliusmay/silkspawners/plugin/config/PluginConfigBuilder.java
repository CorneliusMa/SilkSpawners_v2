package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;

final class PluginConfigBuilder {

    final ConfigScope scope;
    final String key;
    ConfigValueFormatter<?> formatter;
    Object[] defaultValue = new Object[]{null};
    String[] legacyKeys;
    boolean list;
    ConfigValueMigrator migrator;

    PluginConfigBuilder(ConfigScope scope, String key) {
        this.scope = scope;
        this.key = key;
    }

    PluginConfigBuilder formatter(ConfigValueFormatter<?> formatter) {
        this.formatter = formatter;
        return this;
    }

    PluginConfigBuilder defs(Object... values) {
        this.defaultValue = values;
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

    PluginConfigBuilder migrator(ConfigValueMigrator migrator) {
        this.migrator = migrator;
        return this;
    }
}
