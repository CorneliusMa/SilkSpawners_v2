package de.corneliusmay.silkspawners.plugin.hooks;

import de.corneliusmay.silkspawners.api.SpawnerProvider;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

class SilkSpawnersProvider implements SpawnerProvider {

    private final SilkSpawners plugin;

    SilkSpawnersProvider(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack getSpawnerItem(EntityType entityType) {
        return new Spawner(plugin, entityType).getItemStack();
    }

    @Override
    public EntityType getSpawnerEntityType(ItemStack itemStack) {
        return new Spawner(plugin, itemStack).getEntityType();
    }
}
