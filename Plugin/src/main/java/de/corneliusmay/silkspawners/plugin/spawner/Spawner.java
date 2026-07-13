package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import de.corneliusmay.silkspawners.plugin.utils.StringUtils;
import java.util.List;
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

    private final SilkSpawners plugin;

    @Getter
    private EntityType entityType;

    private ItemStack itemStack;

    public Spawner(SilkSpawners plugin, Block block) {
        this.plugin = plugin;
        if (block == null) return;
        if (block.getType() != this.plugin.getBukkitHandler().getSpawnerMaterial()) return;

        CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
        this.entityType = creatureSpawner.getSpawnedType();
        this.itemStack = generateItemStack();
    }

    public Spawner(SilkSpawners plugin, ItemStack itemStack) {
        this.plugin = plugin;
        if (itemStack == null) return;
        this.itemStack = itemStack.clone();
        if (itemStack.getType() != this.plugin.getBukkitHandler().getSpawnerMaterial()) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) return;

        this.entityType = getSpawnerEntity(itemMeta.getLore().get(0));
    }

    public Spawner(SilkSpawners plugin, EntityType entityType) {
        this.plugin = plugin;
        this.entityType = entityType;
        this.itemStack = generateItemStack();
    }

    public Spawner(SilkSpawners plugin, SpawnerSnapshot snapshot) {
        this(plugin, snapshot.getEntityType());
    }

    public static Spawner of(SilkSpawners plugin, SpawnerSnapshot snapshot) {
        return snapshot instanceof Spawner spawner ? spawner : new Spawner(plugin, snapshot);
    }

    public ItemStack getItemStack() {
        return itemStack == null ? null : itemStack.clone();
    }

    public void setSpawnerBlockType(Block block, Set<Location> editedList) {
        setSpawnerBlockType(block, () -> editedList.remove(block.getLocation()));
    }

    private void setSpawnerBlockType(Block block, Runnable removeFromEditedList) {
        if (!isValid()) {
            removeFromEditedList.run();
            return;
        }
        this.plugin
                .getPlatform()
                .runTaskLater(
                        block.getLocation(),
                        () -> {
                            BlockState blockState = block.getState();
                            if (!(blockState instanceof CreatureSpawner)) return;
                            CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
                            creatureSpawner.setSpawnedType(this.entityType);
                            blockState.update();
                            removeFromEditedList.run();
                        },
                        1);
    }

    private ItemStack generateItemStack() {
        return new ItemBuilder(this.plugin.getBukkitHandler().getSpawnerMaterial())
                .setDisplayName(new ConfigValue<String>(PluginConfig.SPAWNER_ITEM_NAME).get())
                .addToLore(serializedName())
                .addToLore(new ConfigValue<List<String>>(PluginConfig.SPAWNER_ITEM_LORE).get())
                .build();
    }

    private String getPrefix() {
        return new ConfigValue<String>(PluginConfig.SPAWNER_ITEM_PREFIX).get();
    }

    private EntityType getSpawnerEntity(String lore) {
        String name;
        String prefix = getPrefix();
        String oldPrefix = new ConfigValue<String>(PluginConfig.SPAWNER_ITEM_PREFIX_OLD).get();
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

    public boolean isValid() {
        return itemStack != null && (isEmpty() || entityType.isSpawnable());
    }

    public boolean isEmpty() {
        return entityType == null;
    }
}
