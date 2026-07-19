package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

@RequiredArgsConstructor
public class SpawnerPrefixOldMigrator implements ConfigValueMigrator {

    private final String prefixPath;
    private final String legacyPrefixDefault;

    @Override
    public Object migrate(Object legacyValue, ConfigurationSection legacyConfig) {
        List<String> prefixes = new ArrayList<>();
        if (legacyValue instanceof String prefix && !prefix.isEmpty()) prefixes.add(prefix);
        Object legacyPrefix = legacyConfig.get(prefixPath);
        if ((legacyPrefix == null || legacyPrefixDefault.equals(legacyPrefix))
                && !prefixes.contains(legacyPrefixDefault)) prefixes.add(legacyPrefixDefault);
        return prefixes;
    }
}
