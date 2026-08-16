package de.corneliusmay.silkspawners.bukkit.spawnegg.legacy;

import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface SpawnEggLegacyVersionAdapter extends VersionAdapter {

    @Override
    default boolean isSpawnEgg(ItemStack item) {
        return item.getType() == Material.MONSTER_EGG;
    }

    @Override
    default EntityType spawnEggEntityType(ItemStack item) {
        return null;
    }
}
