package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.api.SpawnerSettings;
import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor
public class SpawnerFactory {

    // The stored location of every spawner item's identity. Changing this orphans all existing items
    private static final String ENTITY_TAG = "silkspawners:entity";

    private static final String SETTINGS_TAG = "silkspawners:settings";

    private final VersionAdapter versionAdapter;

    private final ServerPlatform platform;

    private final EditedSpawners editedSpawners;

    private final Logger logger;

    public Optional<Spawner> fromBlock(Block block) {
        if (block == null || block.getType() != versionAdapter.getSpawnerMaterial()) return Optional.empty();
        CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
        return ofType(creatureSpawner.getSpawnedType(), versionAdapter.readSpawnerSettings(creatureSpawner));
    }

    public Optional<Spawner> fromItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != versionAdapter.getSpawnerMaterial()) return Optional.empty();
        ItemStack item = itemStack.clone();
        Map<String, String> tags = versionAdapter.readTags(item, ENTITY_TAG, SETTINGS_TAG);
        String entityName = tags.get(ENTITY_TAG);
        if (entityName != null) {
            EntityType entityType = EntityNames.resolve(entityName);
            if (entityType == null && !entityName.equals(Spawner.EMPTY)) {
                logger.warn("Ignoring spawner item with unrecognized entity '" + entityName
                        + "' (not supported on this server version)");
                return Optional.empty();
            }
            SpawnerSettings settings = SpawnerSettingsFormat.deserialize(tags.get(SETTINGS_TAG));
            return validated(new Spawner(entityType, item, SpawnerSettingsFormat.nonDefault(settings)));
        }
        return validated(new Spawner(parseLegacyEntityType(item), item, null));
    }

    public ItemStack itemFor(EntityType entityType) {
        return itemFor(entityType, null);
    }

    public ItemStack itemFor(EntityType entityType, SpawnerSettings settings) {
        requireValid(settings);
        return ofType(entityType, settings).map(Spawner::getItemStack).orElse(null);
    }

    public EntityType entityTypeOf(ItemStack itemStack) {
        return fromItem(itemStack).map(Spawner::getEntityType).orElse(null);
    }

    public Optional<Spawner> ofType(EntityType entityType) {
        return ofType(entityType, null);
    }

    public Optional<Spawner> ofType(EntityType entityType, SpawnerSettings settings) {
        settings = SpawnerSettingsFormat.nonDefault(settings);
        ItemBuilder itemBuilder = new ItemBuilder(versionAdapter.getSpawnerMaterial())
                .setDisplayName(Spawner.itemName(entityType))
                .addToLore(Spawner.itemLore(entityType))
                .addItemFlags(versionAdapter.getHideAdditionalTooltipFlag())
                .writeTag(versionAdapter, ENTITY_TAG, Spawner.serializedEntityType(entityType));
        if (settings != null)
            itemBuilder.writeTag(versionAdapter, SETTINGS_TAG, SpawnerSettingsFormat.serialize(settings));
        return validated(new Spawner(entityType, itemBuilder.build(), settings));
    }

    public Spawner snapshot(EntityType entityType, SpawnerSettings settings) {
        requireValid(settings);
        return ofType(entityType, settings)
                .orElseThrow(() -> new IllegalArgumentException("Entity type " + entityType + " is not spawnable"));
    }

    public Spawner of(SpawnerSnapshot snapshot) {
        return snapshot instanceof Spawner spawner
                ? spawner
                : snapshot(snapshot.getEntityType(), snapshot.getSettings());
    }

    public void applyToBlock(Spawner spawner, Block block) {
        applyToBlock(spawner, block, spawner.getSettings());
    }

    // Null settings keep the block's current values
    public void applyToBlock(Spawner spawner, Block block, SpawnerSettings settings) {
        requireValid(settings);
        platform.runTaskLater(
                block.getLocation(),
                () -> {
                    try {
                        BlockState blockState = block.getState();
                        if (!(blockState instanceof CreatureSpawner creatureSpawner)) return;
                        creatureSpawner.setSpawnedType(spawner.getEntityType());
                        if (settings != null) versionAdapter.applySpawnerSettings(creatureSpawner, settings);
                        blockState.update();
                    } finally {
                        editedSpawners.endEdit(block.getLocation());
                    }
                },
                1);
    }

    private Optional<Spawner> validated(Spawner spawner) {
        return spawner.isValid() ? Optional.of(spawner) : Optional.empty();
    }

    private static void requireValid(SpawnerSettings settings) {
        if (settings != null && !SpawnerSettingsFormat.isValid(settings))
            throw new IllegalArgumentException("Invalid spawner settings " + settings);
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
