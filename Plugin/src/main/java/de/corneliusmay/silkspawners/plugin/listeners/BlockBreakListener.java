package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.events.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.utils.Explosion;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener extends SilkSpawnersListener<BlockBreakEvent> {

    @Override @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(BlockBreakEvent e) {
        if(e.isCancelled()) return;

        Spawner spawner = new Spawner(plugin, e.getBlock());
        if(!spawner.isValid()){
            return;
        }

        Player p = e.getPlayer();
        if(!p.hasPermission("silkspawners.break." + spawner.serializedEntityType())
                && !p.hasPermission("silkspawners.break.*")
                && !new ConfigValue<Boolean>(PluginConfig.SPAWNER_PERMISSION_DISABLE_DESTROY).get()) {
            destroySpawner(p, e);
            return;
        }

        ItemStack[] itemsInHand = plugin.getBukkitHandler().getItemsInHand(p);
        if(!itemHasSilktouch(itemsInHand)){
            destroySpawner(p, e);
            return;
        }

        int dropChance = new ConfigValue<Integer>(PluginConfig.SPAWNER_DROP_CHANCE).get();
        if(Math.random() > dropChance / 100F) {
            destroySpawner(p, e);
            return;
        }

        SpawnerBreakEvent event = new SpawnerBreakEvent(p, spawner, e.getBlock().getLocation(), plugin);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            e.setCancelled(true);
            return;
        }
        e.setExpToDrop(0);
        p.getWorld().dropItemNaturally(e.getBlock().getLocation(), event.getSpawner().getItemStack());
    }

    private void destroySpawner(Player p, BlockBreakEvent e) {
        if(!new ConfigValue<Boolean>(PluginConfig.SPAWNER_DESTROYABLE).get()) {
            e.setCancelled(true);
            if(new ConfigValue<Boolean>(PluginConfig.SPAWNER_MESSAGE_DENY_DESTROY).get()) p.sendMessage(plugin.getLocale().getMessage("SPAWNER_DESTROY_DENIED"));
        }
        else new Explosion(p, e.getBlock().getWorld(), e.getBlock().getLocation(), new ConfigValue<Integer>(PluginConfig.SPAWNER_EXPLOSION_NORMAL).get());
    }

    private boolean itemHasSilktouch(ItemStack[] items) {
        return itemHasSilktouch(items, 0);
    }

    private boolean itemHasSilktouch(ItemStack[] items, int i) {
        if(items.length == i) return false;

        boolean isPickaxe = items[i].getType().toString().contains("PICKAXE")
                || !new ConfigValue<Boolean>(PluginConfig.SPAWNER_PICKAXE_REQUIRED).get();
        boolean hasSilktouch = items[i].containsEnchantment(Enchantment.SILK_TOUCH)
                || !new ConfigValue<Boolean>(PluginConfig.SPAWNER_SILKTOUCH_REQUIRED).get();
        if(isPickaxe && hasSilktouch) return true;

        return itemHasSilktouch(items, i + 1);
    }
}
