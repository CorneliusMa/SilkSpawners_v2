package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.api.events.SpawnerDropEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.explosion.Explosion;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.spawner.SilkDropCheck;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener extends SilkSpawnersListener<BlockBreakEvent> {

    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(BlockBreakEvent e) {
        if (e.isCancelled()) return;

        Spawner spawner = new Spawner(plugin, e.getBlock());
        if (!spawner.isValid()) {
            return;
        }

        Player p = e.getPlayer();
        if (!new SilkDropCheck(plugin).canSilkDrop(p, spawner)) {
            destroySpawner(p, e, spawner);
            return;
        }

        int dropChance = PluginConfig.SPAWNER_DROP_CHANCE.get();
        SpawnerDropEvent dropEvent = new SpawnerDropEvent(
                p,
                spawner,
                e.getBlock().getLocation(),
                spawner.getItemStack(),
                dropChance,
                type -> new Spawner(plugin, type));
        Bukkit.getPluginManager().callEvent(dropEvent);

        if (dropEvent.isCancelled()) return;

        if (Math.random() * 100 > dropEvent.getDropChance()) {
            destroySpawner(p, e, spawner);
            return;
        }

        SpawnerBreakEvent event =
                new SpawnerBreakEvent(p, spawner, e.getBlock().getLocation(), type -> new Spawner(plugin, type));
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        e.setExpToDrop(0);

        ItemStack spawnerItem = !dropEvent.hasCustomDrop() && event.hasReplacedSpawner()
                ? event.getSpawner().getItemStack()
                : dropEvent.getDrop();
        if (new Explosion(PluginConfig.SPAWNER_EXPLOSION_SILKTOUCH).applies(p)) {
            plugin.getPlatform().runTaskLater(e.getBlock().getLocation(), () -> dropItem(e, spawnerItem), 2);
        } else {
            p.getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerItem);
        }
    }

    private void destroySpawner(Player p, BlockBreakEvent e, Spawner spawner) {
        if (!PluginConfig.SPAWNER_DESTROYABLE.get()) {
            e.setCancelled(true);
            if (PluginConfig.SPAWNER_MESSAGE_DENY_DESTROY.get())
                p.sendMessage(plugin.getLocale().getMessage("SPAWNER_DESTROY_DENIED"));
            return;
        }

        Explosion explosion = new Explosion(PluginConfig.SPAWNER_EXPLOSION_NORMAL);
        if (!explosion.applies(p)) return;
        plugin.getPlatform().runTaskLater(e.getBlock().getLocation(), () -> runExplosion(explosion, p, e, spawner), 1);
    }

    private void dropItem(BlockBreakEvent e, ItemStack spawnerItem) {
        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerItem);
    }

    private void runExplosion(Explosion explosion, Player p, BlockBreakEvent e, Spawner spawner) {
        if (e.isCancelled()) return;
        explosion.run(p, e.getBlock().getWorld(), e.getBlock().getLocation(), spawner);
    }
}
