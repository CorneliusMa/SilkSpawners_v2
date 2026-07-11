package de.corneliusmay.silkspawners.api;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface SpawnerProvider {

    ItemStack getSpawnerItem(EntityType entityType);

    EntityType getSpawnerEntityType(ItemStack itemStack);

}
