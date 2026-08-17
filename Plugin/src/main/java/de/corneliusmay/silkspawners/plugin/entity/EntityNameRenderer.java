package de.corneliusmay.silkspawners.plugin.entity;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.entity.EntityType;
import org.weftkit.wiring.Wired;

@Wired
public class EntityNameRenderer {

    public String colored(EntityType entityType) {
        return PluginConfig.SPAWNER_ITEM_COLOR.get() + Spawner.displayName(entityType);
    }
}
