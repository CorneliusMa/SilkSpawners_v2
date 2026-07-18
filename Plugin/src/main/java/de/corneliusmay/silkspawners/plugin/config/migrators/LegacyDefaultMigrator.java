package de.corneliusmay.silkspawners.plugin.config.migrators;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

@RequiredArgsConstructor
public class LegacyDefaultMigrator implements ConfigValueMigrator {

    private final Object legacyDefault;
    private final Object currentDefault;

    @Override
    public Object migrate(Object legacyValue, ConfigurationSection legacyConfig) {
        return legacyDefault.equals(legacyValue) ? currentDefault : null;
    }
}
