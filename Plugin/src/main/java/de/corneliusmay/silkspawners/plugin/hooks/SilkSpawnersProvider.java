package de.corneliusmay.silkspawners.plugin.hooks;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.spi.hooks.SpawnerProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

class SilkSpawnersProvider implements SpawnerProvider {

    private final SilkSpawners plugin;

    SilkSpawnersProvider(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack getSpawnerItem(EntityType entityType) {
        return Spawner.ofType(plugin, entityType).map(Spawner::getItemStack).orElse(null);
    }

    @Override
    public EntityType getSpawnerEntityType(ItemStack itemStack) {
        return Spawner.fromItem(plugin, itemStack).map(Spawner::getEntityType).orElse(null);
    }
}
