package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.isCancelled()) return;

        Player p = e.getPlayer();

        ItemStack[] itemsInHand = SilkSpawners.getInstance().getNmsHandler().getItemsInHand(p);
        Spawner spawner = new Spawner(itemIsSpawner(itemsInHand));
        if(!spawner.isValid()) return;

        if(!p.hasPermission("silkspawners.place." + spawner.getEntityType().getName())) {
            if(!new ConfigValue<Boolean>(PluginConfig.SPAWNER_DESTROYABLE).get()) {
                e.setCancelled(true);
                if(new ConfigValue<Boolean>(PluginConfig.SPAWNER_MESSAGE_DENY_PLACE).get()) p.sendMessage(SilkSpawners.getInstance().getLocale().getMessage("SPAWNER_PLACE_DENIED"));
            }
            return;
        }

        SpawnerPlaceEvent event = new SpawnerPlaceEvent(p, spawner.getEntityType(), e.getBlock());
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        spawner.setSpawnerBlockType(e.getBlock());
    }

    private ItemStack itemIsSpawner(ItemStack[] items) {
        return itemIsSpawner(items, 0);
    }

    private ItemStack itemIsSpawner(ItemStack[] items, int i) {
        if(items.length == i) return null;

        if(items[i].getType() == SilkSpawners.getInstance().getNmsHandler().getSpawnerMaterial()) return items[i];
        else return itemIsSpawner(items, i + 1);
    }
}
