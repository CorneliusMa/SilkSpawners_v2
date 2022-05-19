package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Spawner {

    @Getter
    private EntityType entityType;

    @Getter
    private ItemStack itemStack;

    public Spawner(Block block) {
        if(block == null) return;
        if(block.getType() != SilkSpawners.getInstance().getNmsHandler().getSpawnerMaterial()) return;

        CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
        this.entityType = creatureSpawner.getSpawnedType();
        this.itemStack = generateItemStack();
    }

    public Spawner(ItemStack itemStack) {
        this.itemStack = itemStack;
        if(itemStack == null) return;
        if(itemStack.getType() != SilkSpawners.getInstance().getNmsHandler().getSpawnerMaterial()) return;
        if(itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) return;

        this.entityType = getSpawnerEntity(itemStack.getItemMeta().getLore().get(0));
    }

    public Spawner(EntityType entityType) {
        this.entityType = entityType;
        this.itemStack = generateItemStack();
    }

    public void setSpawnerBlockType(Block block) {
        if(!isValid()) return;
        Bukkit.getScheduler().runTaskLater(SilkSpawners.getInstance(), () -> {
            BlockState blockState = block.getState();
            if(!(blockState instanceof CreatureSpawner)) return;
            CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
            creatureSpawner.setSpawnedType(this.entityType);
            blockState.update();
        }, 5);
    }

    private ItemStack generateItemStack() {
        if(this.entityType == null || this.entityType.getName() == null) return null;
        return new ItemBuilder(SilkSpawners.getInstance().getNmsHandler().getSpawnerMaterial())
                .addToLore("§e" + this.entityType.getName().substring(0, 1).toUpperCase() + this.entityType.getName().substring(1)).build();
    }

    private EntityType getSpawnerEntity(String lore) {
        if(!lore.startsWith("§e")) return null;
        return EntityType.fromName(lore.replaceAll("§e", "").toLowerCase());
    }

    public boolean isValid() {
        return itemStack != null && entityType != null && entityType.isSpawnable();
    }
}
