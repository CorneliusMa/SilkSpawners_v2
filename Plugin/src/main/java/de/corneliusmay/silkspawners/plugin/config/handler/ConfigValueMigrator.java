package de.corneliusmay.silkspawners.plugin.config.handler;

@FunctionalInterface
public interface ConfigValueMigrator {
    Object migrate(Object legacyValue);
}
