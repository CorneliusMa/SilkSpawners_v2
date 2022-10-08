package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class PlayerInteractListener extends SilkSpawnersListener<PlayerInteractEvent> {

    private final List<Block> editedSpawners;

    public PlayerInteractListener(List<Block> editedSpawners) {
        this.editedSpawners = editedSpawners;
    }

    @Override @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();

        Spawner spawner = new Spawner(plugin, block);
        if(!spawner.isValid()) return;

        if(editedSpawners.stream().anyMatch(b -> b.getLocation().equals(block.getLocation()))) {
            e.setCancelled(true);
            return;
        }
        editedSpawners.add(block);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Spawner newSpawner = new Spawner(plugin, block.getWorld().getBlockAt(block.getLocation()));

            if(!e.getPlayer().hasPermission("silkspawners.change." + newSpawner.getEntityType().getName()) && !e.getPlayer().hasPermission("silkspawners.change.*") && spawner.getEntityType() != newSpawner.getEntityType()) {
                spawner.setSpawnerBlockType(block, this.editedSpawners);
                if(new ConfigValue<Boolean>(PluginConfig.SPAWNER_MESSAGE_DENY_CHANGE).get()) e.getPlayer().sendMessage(plugin.getLocale().getMessage("SPAWNER_CHANGE_DENIED"));
            }
        }, 1);
    }
}
