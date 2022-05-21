package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.utils.Explosion;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.isCancelled()) return;

        Spawner spawner = new Spawner(e.getBlock());
        if(!spawner.isValid()) return;

        Player p = e.getPlayer();
        if(!p.hasPermission("silkspawners.break." + spawner.getEntityType().getName())) {
            destroySpawner(p, e);
            return;
        }

        ItemStack[] itemsInHand = SilkSpawners.getInstance().getNmsHandler().getItemsInHand(p);
        if(!itemHasSilktouch(itemsInHand)) {
            destroySpawner(p, e);
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

    private void destroySpawner(Player p, BlockBreakEvent e) {
        if(!SilkSpawners.getInstance().getPluginConfig().isSpawnerDestroyable()) {
            e.setCancelled(true);
            if(SilkSpawners.getInstance().getPluginConfig().sendSpawnerDestroyMessage()) p.sendMessage(SilkSpawners.getInstance().getLocale().getMessage("SPAWNER_DESTROY_DENIED"));
        }
        else new Explosion(p, e.getBlock().getWorld(), e.getBlock().getLocation(), SilkSpawners.getInstance().getPluginConfig().getSpawnerExplosion());
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
