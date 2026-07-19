package de.corneliusmay.silkspawners.plugin.config.migrators;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

@RequiredArgsConstructor
public class InheritValueMigrator implements ConfigValueMigrator {

    private final String legacyPath;

    @Override
    public Object migrate(Object legacyValue, ConfigurationSection legacyConfig) {
        return legacyValue == null ? legacyConfig.get(legacyPath) : null;
    }
}
