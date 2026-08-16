package de.corneliusmay.silkspawners.bukkit.spawnegg;

import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.Map;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface SpawnEggVersionAdapter extends VersionAdapter {

    String SPAWN_EGG_SUFFIX = "_SPAWN_EGG";

    Map<String, String> LEGACY_ENTITY_NAMES = Map.of(
            "MOOSHROOM", "MUSHROOM_COW",
            "ZOMBIE_PIGMAN", "PIG_ZOMBIE");

    @Override
    default boolean isSpawnEgg(ItemStack item) {
        return item.getType().name().endsWith(SPAWN_EGG_SUFFIX);
    }

    @Override
    default EntityType spawnEggEntityType(ItemStack item) {
        if (!isSpawnEgg(item)) return null;
        String materialName = item.getType().name();
        String entityName = materialName.substring(0, materialName.length() - SPAWN_EGG_SUFFIX.length());
        EntityType entityType = entityTypeByName(entityName);
        return entityType != null ? entityType : entityTypeByName(LEGACY_ENTITY_NAMES.get(entityName));
    }

    private static EntityType entityTypeByName(String name) {
        if (name == null) return null;
        try {
            return EntityType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
