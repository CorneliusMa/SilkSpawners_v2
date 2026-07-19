package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.spi.version.Bukkit;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Wired
@RequiredArgsConstructor
public class SpawnerFactory {

    private final Bukkit bukkitHandler;

    private final ServerPlatform platform;

    public Optional<Spawner> fromBlock(Block block) {
        if (block == null || block.getType() != bukkitHandler.getSpawnerMaterial()) return Optional.empty();
        CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
        return ofType(creatureSpawner.getSpawnedType());
    }

    public Optional<Spawner> fromItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != bukkitHandler.getSpawnerMaterial()) return Optional.empty();
        return validated(new Spawner(parseEntityType(itemStack), itemStack.clone()));
    }

    public ItemStack itemFor(EntityType entityType) {
        return ofType(entityType).map(Spawner::getItemStack).orElse(null);
    }

    public EntityType entityTypeOf(ItemStack itemStack) {
        return fromItem(itemStack).map(Spawner::getEntityType).orElse(null);
    }

    public Optional<Spawner> ofType(EntityType entityType) {
        ItemStack itemStack = new ItemBuilder(bukkitHandler.getSpawnerMaterial())
                .setDisplayName(Spawner.itemName(entityType))
                .addToLore(Spawner.serializedName(entityType))
                .addToLore(PluginConfig.SPAWNER_ITEM_LORE.get())
                .build();
        return validated(new Spawner(entityType, itemStack));
    }

    public Spawner snapshot(EntityType entityType) {
        return ofType(entityType)
                .orElseThrow(() -> new IllegalArgumentException("Entity type " + entityType + " is not spawnable"));
    }

    public Spawner of(SpawnerSnapshot snapshot) {
        return snapshot instanceof Spawner spawner ? spawner : snapshot(snapshot.getEntityType());
    }

    public void applyToBlock(Spawner spawner, Block block, Set<Location> editedList) {
        platform.runTaskLater(
                block.getLocation(),
                () -> {
                    BlockState blockState = block.getState();
                    if (!(blockState instanceof CreatureSpawner creatureSpawner)) return;
                    creatureSpawner.setSpawnedType(spawner.getEntityType());
                    blockState.update();
                    editedList.remove(block.getLocation());
                },
                1);
    }

    private Optional<Spawner> validated(Spawner spawner) {
        return spawner.isValid() ? Optional.of(spawner) : Optional.empty();
    }

    private EntityType parseEntityType(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) return null;
        return parseEntityType(itemMeta.getLore().get(0));
    }

    private EntityType parseEntityType(String lore) {
        String prefix = PluginConfig.SPAWNER_ITEM_PREFIX.get();
        if (lore.startsWith(prefix)) return entityTypeFromName(lore.substring(prefix.length()));
        for (String oldPrefix : PluginConfig.SPAWNER_ITEM_PREFIX_OLD.get()) {
            if (!oldPrefix.isEmpty() && lore.startsWith(oldPrefix))
                return entityTypeFromName(lore.substring(oldPrefix.length()));
        }
        return null; // Invalid lore
    }

    private EntityType entityTypeFromName(String displayName) {
        String name = displayName.replace(" ", "_").toLowerCase();
        if (name.equalsIgnoreCase(Spawner.EMPTY)) return null;
        return EntityType.fromName(name);
    }
}
