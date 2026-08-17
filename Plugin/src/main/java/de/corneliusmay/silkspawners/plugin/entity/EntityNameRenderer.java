package de.corneliusmay.silkspawners.plugin.entity;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
public class EntityNameRenderer {

    private final PluginConfig config;

    public String colored(EntityType entityType) {
        return config.SPAWNER_ITEM_COLOR.get() + EntityNames.displayName(entityType);
    }
}
