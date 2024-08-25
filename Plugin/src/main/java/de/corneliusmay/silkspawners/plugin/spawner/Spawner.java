package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueArray;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import de.corneliusmay.silkspawners.plugin.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Spawner {
    public static String EMPTY = "empty";

    private final SilkSpawners plugin;

    @Getter
    private EntityType entityType;

    @Getter
    private ItemStack itemStack;

    private final String prefix = new ConfigValue<String>(PluginConfig.SPAWNER_ITEM_PREFIX).get();
    private final String oldPrefix = new ConfigValue<String>(PluginConfig.SPAWNER_ITEM_PREFIX_OLD).get();

    public Spawner(SilkSpawners plugin, Block block) {
        this.plugin = plugin;
        if(block == null) return;
        if(block.getType() != this.plugin.getBukkitHandler().getSpawnerMaterial()) return;

        CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
        this.entityType = creatureSpawner.getSpawnedType();
        this.itemStack = generateItemStack();
    }

    public Spawner(SilkSpawners plugin, ItemStack itemStack) {
        this.plugin = plugin;
        this.itemStack = itemStack;
        if(itemStack == null) return;
        if(itemStack.getType() != this.plugin.getBukkitHandler().getSpawnerMaterial()) return;
        if(itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) return;

        this.entityType = getSpawnerEntity(itemStack.getItemMeta().getLore().get(0));
    }

    public Spawner(SilkSpawners plugin, EntityType entityType) {
        this.plugin = plugin;
        this.entityType = entityType;
        this.itemStack = generateItemStack();
    }

    public void setSpawnerBlockType(Block block, List<Block> editedList) {
        if(!isValid()){
            editedList.remove(block);
            return;
        }
        this.plugin.getPlatform().runTaskLater(block.getLocation(), () -> {
            BlockState blockState = block.getState();
            if(!(blockState instanceof CreatureSpawner)) return;
            CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
            creatureSpawner.setSpawnedType(this.entityType);
            blockState.update();
            editedList.remove(block);
        }, 1);
    }

    private ItemStack generateItemStack() {
        return new ItemBuilder(this.plugin.getBukkitHandler().getSpawnerMaterial())
                .setDisplayName(new ConfigValue<String>(PluginConfig.SPAWNER_ITEM_NAME).get())
                .addToLore(serializedName())
                .addToLore(new ConfigValueArray<String>(PluginConfig.SPAWNER_ITEM_LORE).get()).build();
    }

    private EntityType getSpawnerEntity(String lore) {
        String name;
        if(lore.startsWith(prefix)) {
            name = lore.replaceFirst(prefix, "").replace(" ", "_").toLowerCase();
        }else if(!oldPrefix.equals("") && lore.startsWith(oldPrefix)) {
            name = lore.replaceFirst(oldPrefix, "").replace(" ", "_").toLowerCase();
        }else {
            return null; // Invalid lore
        }
        if (name.equalsIgnoreCase(Spawner.EMPTY)) {
            return null;
        }
        return EntityType.fromName(name);
    }

    public String serializedEntityType() {
        return entityType == null ? Spawner.EMPTY : entityType.getName().toLowerCase();
    }

    public String serializedName() {
        return prefix + StringUtils.capitalizeFully(serializedEntityType().replace("_", " "));
    }

    public boolean isValid() {
        return itemStack != null && (entityType == null || entityType.isSpawnable());
    }
}
