package de.corneliusmay.silkspawners.plugin.api;

import de.corneliusmay.silkspawners.api.SilkSpawnersAPI;
import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.SilkDropCheck;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnableEntities;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;

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
        return Spawner.ofType(plugin, entityType).map(Spawner::getItemStack).orElse(null);
    }

    @Override
    public EntityType getEntityType(ItemStack itemStack) {
        return Spawner.fromItem(plugin, itemStack).map(Spawner::getEntityType).orElse(null);
    }

    @Override
    public boolean isSpawnerItem(ItemStack itemStack) {
        return itemStack != null
                && itemStack.getType() == plugin.getBukkitHandler().getSpawnerMaterial();
    }

    @Override
    public SpawnerSnapshot getSpawner(Block block) {
        return Spawner.fromBlock(plugin, block).orElse(null);
    }

    @Override
    public boolean setSpawnerType(Block block, EntityType entityType) {
        if (!isSpawnerBlock(block)) return false;

        Optional<Spawner> spawner = Spawner.ofType(plugin, entityType);
        if (spawner.isEmpty()) return false;

        spawner.get().setSpawnerBlockType(block, new HashSet<>());
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
        return Spawner.ofType(plugin, entityType)
                .map(spawner -> new SilkDropCheck(plugin).canSilkDrop(player, spawner))
                .orElse(false);
    }
}
