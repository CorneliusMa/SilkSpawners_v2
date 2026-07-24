package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

@RequiredArgsConstructor
public class SpawnerLoreMigrator implements ConfigValueMigrator {

    private final String prefixPath;
    private final String legacyPrefixDefault;
    private final String defaultTemplate;

    @Override
    public Object migrate(Object legacyValue, ConfigurationSection legacyConfig) {
        List<String> lore = new ArrayList<>();
        lore.add(template(legacyConfig.get(prefixPath)));
        if (legacyValue instanceof List<?> lines) lines.forEach(line -> lore.add(String.valueOf(line)));
        return lore;
    }

    private String template(Object legacyPrefix) {
        if (legacyPrefix instanceof String prefix && !prefix.isEmpty() && !legacyPrefixDefault.equals(prefix))
            return prefix + "{entity}";
        return defaultTemplate;
    }
}
