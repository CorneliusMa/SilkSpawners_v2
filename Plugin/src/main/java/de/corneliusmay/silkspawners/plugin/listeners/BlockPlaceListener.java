package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.events.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener extends SilkSpawnersListener<BlockPlaceEvent> {

    @Override @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(BlockPlaceEvent e) {
        if(e.isCancelled()) return;

        Player p = e.getPlayer();

        ItemStack[] itemsInHand = plugin.getNmsHandler().getItemsInHand(p);
        Spawner spawner = new Spawner(plugin, itemIsSpawner(itemsInHand));
        if(!spawner.isValid()) return;

        if(!p.hasPermission("silkspawners.place." + spawner.getEntityType().getName())) {
            e.setCancelled(true);
            if(new ConfigValue<Boolean>(PluginConfig.SPAWNER_MESSAGE_DENY_PLACE).get()) p.sendMessage(plugin.getLocale().getMessage("SPAWNER_PLACE_DENIED"));
            return;
        }

        SpawnerPlaceEvent event = new SpawnerPlaceEvent(p, spawner, e.getBlock().getLocation(), plugin);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        event.getSpawner().setSpawnerBlockType(e.getBlock());
    }

    private ItemStack itemIsSpawner(ItemStack[] items) {
        return itemIsSpawner(items, 0);
    }

    private ItemStack itemIsSpawner(ItemStack[] items, int i) {
        if(items.length == i) return null;

        if(items[i].getType() == plugin.getNmsHandler().getSpawnerMaterial()) return items[i];
        else return itemIsSpawner(items, i + 1);
    }
}
