package de.corneliusmay.silkspawners.spi.hooks;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface SpawnerProvider {

    ItemStack getSpawnerItem(EntityType entityType);

    EntityType getSpawnerEntityType(ItemStack itemStack);

}
