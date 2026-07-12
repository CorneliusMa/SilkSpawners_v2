package de.corneliusmay.silkspawners.plugin.api;

import de.corneliusmay.silkspawners.api.SilkSpawnersAPI;
import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.SilkDropCheck;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnableEntities;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;

import java.util.HashSet;
import java.util.Set;

public class SilkSpawnersService implements SilkSpawnersAPI {

    private final SilkSpawners plugin;

    public SilkSpawnersService(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getServicesManager().register(SilkSpawnersAPI.class, this, plugin, ServicePriority.Normal);
    }

    @Override
    public ItemStack getSpawnerItem(EntityType entityType) {
        Spawner spawner = new Spawner(plugin, entityType);
        return spawner.isValid() ? spawner.getItemStack() : null;
    }

    @Override
    public EntityType getEntityType(ItemStack itemStack) {
        return new Spawner(plugin, itemStack).getEntityType();
    }

    @Override
    public boolean isSpawnerItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == plugin.getBukkitHandler().getSpawnerMaterial();
    }

    @Override
    public SpawnerSnapshot getSpawner(Block block) {
        return isSpawnerBlock(block) ? new Spawner(plugin, block) : null;
    }

    @Override
    public boolean setSpawnerType(Block block, EntityType entityType) {
        if(!isSpawnerBlock(block)) return false;

        Spawner spawner = new Spawner(plugin, entityType);
        if(!spawner.isValid()) return false;
        spawner.setSpawnerBlockType(block, new HashSet<>());
        return true;
    }

    private boolean isSpawnerBlock(Block block) {
        return block != null && block.getType() == plugin.getBukkitHandler().getSpawnerMaterial();
    }

    @Override
    public Set<EntityType> getSupportedEntityTypes() {
        return SpawnableEntities.TYPES;
    }

    @Override
    public boolean canSilkDrop(Player player, EntityType entityType) {
        Spawner spawner = new Spawner(plugin, entityType);
        if(!spawner.isValid()) return false;
        return new SilkDropCheck(plugin).canSilkDrop(player, spawner);
    }
}
