package de.corneliusmay.silkspawners.hooks.shopguiplus;

import de.corneliusmay.silkspawners.api.SpawnerProvider;
import net.brcdev.shopgui.spawner.external.provider.ExternalSpawnerProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ShopGuiPlusSpawner implements ExternalSpawnerProvider {

    private final String name;

    private final SpawnerProvider spawnerProvider;

    public ShopGuiPlusSpawner(String name, SpawnerProvider spawnerProvider) {
        this.name = name;
        this.spawnerProvider = spawnerProvider;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getSpawnerItem(EntityType entityType) {
        return spawnerProvider.getSpawnerItem(entityType);
    }

    @Override
    public EntityType getSpawnerEntityType(ItemStack itemStack) {
        return spawnerProvider.getSpawnerEntityType(itemStack);
    }
}
