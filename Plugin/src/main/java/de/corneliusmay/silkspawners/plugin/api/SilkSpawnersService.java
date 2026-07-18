package de.corneliusmay.silkspawners.plugin.api;

import de.corneliusmay.silkspawners.api.SilkSpawnersAPI;
import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.SilkDropCheck;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnableEntities;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;

@Wired
@RequiredArgsConstructor
public class SilkSpawnersService implements SilkSpawnersAPI {

    private final SilkSpawners plugin;

    private final SpawnerFactory spawnerFactory;

    private final de.corneliusmay.silkspawners.spi.version.Bukkit bukkitHandler;

    private final SilkDropCheck silkDropCheck;

    public void register() {
        plugin.getServer().getServicesManager().register(SilkSpawnersAPI.class, this, plugin, ServicePriority.Normal);
    }

    @Override
    public ItemStack getSpawnerItem(EntityType entityType) {
        return spawnerFactory.itemFor(entityType);
    }

    @Override
    public EntityType getEntityType(ItemStack itemStack) {
        return spawnerFactory.entityTypeOf(itemStack);
    }

    @Override
    public boolean isSpawnerItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == bukkitHandler.getSpawnerMaterial();
    }

    @Override
    public SpawnerSnapshot getSpawner(Block block) {
        return spawnerFactory.fromBlock(block).orElse(null);
    }

    @Override
    public boolean setSpawnerType(Block block, EntityType entityType) {
        if (!isSpawnerBlock(block)) return false;

        Optional<Spawner> spawner = spawnerFactory.ofType(entityType);
        if (spawner.isEmpty()) return false;

        spawnerFactory.applyToBlock(spawner.get(), block, new HashSet<>());
        return true;
    }

    private boolean isSpawnerBlock(Block block) {
        return block != null && block.getType() == bukkitHandler.getSpawnerMaterial();
    }

    @Override
    public Set<EntityType> getSupportedEntityTypes() {
        return SpawnableEntities.TYPES;
    }

    @Override
    public boolean canSilkDrop(Player player, EntityType entityType) {
        return spawnerFactory
                .ofType(entityType)
                .map(spawner -> silkDropCheck.canSilkDrop(player, spawner))
                .orElse(false);
    }
}
