package de.corneliusmay.silkspawners.plugin.config.handler;

import org.bukkit.configuration.ConfigurationSection;

@FunctionalInterface
public interface ConfigValueMigrator {
    Object migrate(Object legacyValue, ConfigurationSection legacyConfig);
}
