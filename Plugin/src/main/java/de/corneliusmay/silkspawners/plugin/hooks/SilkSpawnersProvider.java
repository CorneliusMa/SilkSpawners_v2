package de.corneliusmay.silkspawners.plugin.hooks;

import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.spi.hooks.SpawnerProvider;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
class SilkSpawnersProvider implements SpawnerProvider {

    private final SpawnerFactory spawnerFactory;

    @Override
    public ItemStack getSpawnerItem(EntityType entityType) {
        return spawnerFactory.itemFor(entityType);
    }

    @Override
    public EntityType getSpawnerEntityType(ItemStack itemStack) {
        return spawnerFactory.entityTypeOf(itemStack);
    }
}
