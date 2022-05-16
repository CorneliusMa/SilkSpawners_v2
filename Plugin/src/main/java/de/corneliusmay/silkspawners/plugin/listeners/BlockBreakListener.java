package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        if(e.isCancelled()) return;

        if(!p.hasPermission("silkspawners.break")) return;
        if(e.getBlock().getType() != SilkSpawners.getInstance().getNmsHandler().getSpawnerMaterial()) return;


        ItemStack[] itemsInHand = SilkSpawners.getInstance().getNmsHandler().getItemsInHand(p);

        if(!itemHasSilktouch(itemsInHand)) return;

        Block spawnerBlock = e.getBlock();
        CreatureSpawner creatureSpawner = (CreatureSpawner) spawnerBlock.getState();
        EntityType spawnedEntity = creatureSpawner.getSpawnedType();

        if(spawnedEntity.getName() == null) return;

        ItemStack spawnerItemStack = new ItemBuilder(SilkSpawners.getInstance().getNmsHandler().getSpawnerMaterial()).addToLore("§e" + spawnedEntity.getName().substring(0, 1).toUpperCase() + spawnedEntity.getName().substring(1)).build();

        SpawnerBreakEvent event = new SpawnerBreakEvent(p, spawnedEntity, e.getBlock());
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        e.setExpToDrop(0);
        p.getWorld().dropItemNaturally(spawnerBlock.getLocation(), spawnerItemStack);
    }

    private boolean itemHasSilktouch(ItemStack[] items) {
        return itemHasSilktouch(items, 0);
    }
    private boolean itemHasSilktouch(ItemStack[] items, int i) {
        if(items.length == i) return false;

        if(items[i].containsEnchantment(Enchantment.SILK_TOUCH)) return true;
        else return itemHasSilktouch(items, i + 1);
    }
}
