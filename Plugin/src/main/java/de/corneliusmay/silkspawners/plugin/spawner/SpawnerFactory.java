package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import de.corneliusmay.silkspawners.wiring.Loader;
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
public class SpawnerFactory implements Loader {

    // The stored location of every spawner item's identity. Changing this orphans all existing items
    private static final String ENTITY_TAG = "silkspawners:entity";

    private final VersionAdapter versionAdapter;

    private final ServerPlatform platform;

    @Override
    public boolean load() {
        return true;
    }

    public Optional<Spawner> fromBlock(Block block) {
        if (block == null || block.getType() != versionAdapter.getSpawnerMaterial()) return Optional.empty();
        CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
        return ofType(creatureSpawner.getSpawnedType());
    }

    public Optional<Spawner> fromItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != versionAdapter.getSpawnerMaterial()) return Optional.empty();
        ItemStack item = itemStack.clone();
        String entityName = versionAdapter.readTag(item, ENTITY_TAG);
        if (entityName != null) {
            EntityType entityType = EntityNames.resolve(entityName);
            if (entityType == null && !entityName.equals(Spawner.EMPTY)) {
                Logger.warn("Ignoring spawner item with unrecognized entity '" + entityName
                        + "' (not supported on this server version)");
                return Optional.empty();
            }
            return validated(new Spawner(entityType, item));
        }
        return validated(new Spawner(parseLegacyEntityType(item), item));
    }

    public ItemStack itemFor(EntityType entityType) {
        return ofType(entityType).map(Spawner::getItemStack).orElse(null);
    }

    public EntityType entityTypeOf(ItemStack itemStack) {
        return fromItem(itemStack).map(Spawner::getEntityType).orElse(null);
    }

    public Optional<Spawner> ofType(EntityType entityType) {
        ItemStack itemStack = new ItemBuilder(versionAdapter.getSpawnerMaterial())
                .setDisplayName(Spawner.itemName(entityType))
                .addToLore(Spawner.itemLore(entityType))
                .addItemFlags(versionAdapter.getHideAdditionalTooltipFlag())
                .writeTag(versionAdapter, ENTITY_TAG, Spawner.serializedEntityType(entityType))
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

    private EntityType parseLegacyEntityType(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) return null;
        String lore = itemMeta.getLore().get(0);
        for (String oldPrefix : PluginConfig.SPAWNER_ITEM_PREFIX_OLD.get()) {
            if (!oldPrefix.isEmpty() && lore.startsWith(oldPrefix))
                return entityTypeFromName(lore.substring(oldPrefix.length()));
        }
        return null; // Invalid lore
    }

    private EntityType entityTypeFromName(String displayName) {
        String name = displayName.replace(" ", "_").toLowerCase();
        if (name.equalsIgnoreCase(Spawner.EMPTY)) return null;
        return EntityNames.resolve(name);
    }
}
