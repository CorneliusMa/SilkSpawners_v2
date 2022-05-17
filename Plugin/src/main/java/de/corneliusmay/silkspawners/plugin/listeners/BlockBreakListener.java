package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.isCancelled()) return;

        Spawner spawner = new Spawner(e.getBlock());
        if(!spawner.isValid()) return;

        Player p = e.getPlayer();
        if(!p.hasPermission("silkspawners.break")) return;

        ItemStack[] itemsInHand = SilkSpawners.getInstance().getNmsHandler().getItemsInHand(p);
        if(!itemHasSilktouch(itemsInHand)) {
            generateExplosion(e.getBlock());
            return;
        }

        SpawnerBreakEvent event = new SpawnerBreakEvent(p, spawner.getEntityType(), e.getBlock());
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        e.setExpToDrop(0);
        p.getWorld().dropItemNaturally(e.getBlock().getLocation(), spawner.getItemStack());
    }

    private void generateExplosion(Block spawner) {
        int intensity = SilkSpawners.getInstance().getPluginConfig().getSpawnerExplosion();
        if(intensity == 0) return;
        spawner.getWorld().createExplosion(spawner.getLocation(), intensity);
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
