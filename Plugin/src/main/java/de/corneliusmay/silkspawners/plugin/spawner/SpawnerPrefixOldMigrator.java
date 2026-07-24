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
    private final String legacyEmptyPrefixRender;

    @Override
    public Object migrate(Object legacyValue, ConfigurationSection legacyConfig) {
        List<String> prefixes = new ArrayList<>();
        if (legacyValue instanceof String prefix && !prefix.isEmpty()) prefixes.add(prefix);
        for (String prefix : effectivePrefixes(legacyConfig.get(prefixPath))) {
            if (!prefixes.contains(prefix)) prefixes.add(prefix);
        }
        return prefixes;
    }

    private List<String> effectivePrefixes(Object legacyPrefix) {
        if (legacyPrefix instanceof String prefix && !prefix.isEmpty()) return List.of(prefix);
        return List.of(legacyPrefixDefault, legacyEmptyPrefixRender);
    }
}
