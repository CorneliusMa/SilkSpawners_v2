package de.corneliusmay.silkspawners.plugin.config.handler;

@FunctionalInterface
public interface ConfigValueMigrator {

    /**
     * Converts a value written by an older config version into the shape introduced
     * by the config version the migrator is registered for.
     *
     * @param legacyValue the value stored at the entry's path, never {@code null}
     * @return the migrated value, or {@code null} to keep the stored value unchanged
     */
    Object migrate(Object legacyValue);
}
