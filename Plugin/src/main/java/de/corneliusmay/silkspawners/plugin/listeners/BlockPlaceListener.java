package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if(e.isCancelled()) {
            return;
        }

        if(!p.hasPermission("silkspawners.place")) return;

        ItemStack[] itemsInHand = SilkSpawners.getInstance().getNmsHandler().getItemsInHand(p);

        ItemStack spawnerPlaced = itemIsSpawner(itemsInHand);
        if(spawnerPlaced == null) return;

        if(spawnerPlaced.getItemMeta() == null || spawnerPlaced.getItemMeta().getLore() == null) return;

        EntityType spawnerEntityType = getSpawnerEntity(spawnerPlaced.getItemMeta().getLore().get(0));
        if(spawnerEntityType == null) return;

        SpawnerPlaceEvent event = new SpawnerPlaceEvent(p, spawnerEntityType, e.getBlock());
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTaskLater(SilkSpawners.getInstance(), () -> setSpawnerType(e.getBlock(), spawnerEntityType), 5);
    }

    private ItemStack itemIsSpawner(ItemStack[] items) {
        return itemIsSpawner(items, 0);
    }

    private ItemStack itemIsSpawner(ItemStack[] items, int i) {
        if(items.length == i) return null;

        if(items[i].getType() == SilkSpawners.getInstance().getNmsHandler().getSpawnerMaterial()) return items[i];
        else return itemIsSpawner(items, i + 1);
    }

    private EntityType getSpawnerEntity(String lore) {
        if(!lore.startsWith("§e")) return null;

        return EntityType.fromName(lore.replaceAll("§e", "").toLowerCase());
    }

    private void setSpawnerType(Block spawnerBlock, EntityType spawnerEntityType) {
        BlockState blockState = spawnerBlock.getState();
        CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
        creatureSpawner.setSpawnedType(spawnerEntityType);
        blockState.update();
    }
}
