package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import de.corneliusmay.silkspawners.plugin.utils.StringUtils;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Spawner implements SpawnerSnapshot {
    public static final String EMPTY = "empty";

    @Getter
    private EntityType entityType;

    private ItemStack itemStack;

    private Spawner(SpawnerContext context, Block block) {
        if (block == null) return;
        if (block.getType() != context.getBukkitHandler().getSpawnerMaterial()) return;

        CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
        this.entityType = creatureSpawner.getSpawnedType();
        this.itemStack = generateItemStack(context);
    }

    private Spawner(SpawnerContext context, ItemStack itemStack) {
        if (itemStack == null) return;
        this.itemStack = itemStack.clone();
        if (itemStack.getType() != context.getBukkitHandler().getSpawnerMaterial()) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) return;

        this.entityType = getSpawnerEntity(itemMeta.getLore().get(0));
    }

    private Spawner(SpawnerContext context, EntityType entityType) {
        this.entityType = entityType;
        this.itemStack = generateItemStack(context);
    }

    public static Optional<Spawner> fromBlock(Block block) {
        return validated(new Spawner(SpawnerContext.get(), block));
    }

    public static Optional<Spawner> fromItem(ItemStack itemStack) {
        SpawnerContext context = SpawnerContext.get();
        if (itemStack == null
                || itemStack.getType() != context.getBukkitHandler().getSpawnerMaterial()) {
            return Optional.empty();
        }
        return validated(new Spawner(context, itemStack));
    }

    public static Optional<Spawner> ofType(EntityType entityType) {
        return validated(new Spawner(SpawnerContext.get(), entityType));
    }

    public static Spawner snapshot(EntityType entityType) {
        // Events reject non-spawnable entity types before requesting a snapshot
        return ofType(entityType)
                .orElseThrow(() -> new IllegalArgumentException("Entity type " + entityType + " is not spawnable"));
    }

    public static Spawner of(SpawnerSnapshot snapshot) {
        return snapshot instanceof Spawner spawner ? spawner : snapshot(snapshot.getEntityType());
    }

    private static Optional<Spawner> validated(Spawner spawner) {
        return spawner.isValid() ? Optional.of(spawner) : Optional.empty();
    }

    public ItemStack getItemStack() {
        return itemStack == null ? null : itemStack.clone();
    }

    public void setSpawnerBlockType(Block block, Set<Location> editedList) {
        SpawnerContext.get()
                .getPlatform()
                .runTaskLater(
                        block.getLocation(),
                        () -> {
                            BlockState blockState = block.getState();
                            if (!(blockState instanceof CreatureSpawner creatureSpawner)) return;
                            creatureSpawner.setSpawnedType(this.entityType);
                            blockState.update();
                            editedList.remove(block.getLocation());
                        },
                        1);
    }

    private ItemStack generateItemStack(SpawnerContext context) {
        return new ItemBuilder(context.getBukkitHandler().getSpawnerMaterial())
                .setDisplayName(PluginConfig.SPAWNER_ITEM_NAME.get())
                .addToLore(serializedName())
                .addToLore(PluginConfig.SPAWNER_ITEM_LORE.get())
                .build();
    }

    private String getPrefix() {
        return PluginConfig.SPAWNER_ITEM_PREFIX.get();
    }

    private EntityType getSpawnerEntity(String lore) {
        String name;
        String prefix = getPrefix();
        String oldPrefix = PluginConfig.SPAWNER_ITEM_PREFIX_OLD.get();
        if (lore.startsWith(prefix)) {
            name = lore.replaceFirst(prefix, "").replace(" ", "_").toLowerCase();
        } else if (!oldPrefix.equals("") && lore.startsWith(oldPrefix)) {
            name = lore.replaceFirst(oldPrefix, "").replace(" ", "_").toLowerCase();
        } else {
            return null; // Invalid lore
        }
        if (name.equalsIgnoreCase(Spawner.EMPTY)) {
            return null;
        }
        return EntityType.fromName(name);
    }

    public String serializedEntityType() {
        return isEmpty() ? Spawner.EMPTY : entityType.getName().toLowerCase();
    }

    public String serializedName() {
        return getPrefix() + StringUtils.capitalizeFully(serializedEntityType().replace("_", " "));
    }

    private boolean isValid() {
        return itemStack != null && (isEmpty() || entityType.isSpawnable());
    }

    public boolean isEmpty() {
        return entityType == null;
    }
}
