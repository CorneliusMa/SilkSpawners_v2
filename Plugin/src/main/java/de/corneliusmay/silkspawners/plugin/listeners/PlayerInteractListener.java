package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerInteractListener implements Listener {

    private final List<Block> editedSpawners;

    public PlayerInteractListener() {
        this.editedSpawners = Collections.synchronizedList(new ArrayList<>());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        Spawner spawner = new Spawner(block);
        if(!spawner.isValid()) return;

        if(editedSpawners.stream().anyMatch(b -> b.getLocation().equals(block.getLocation()))) {
            e.setCancelled(true);
            return;
        }
        editedSpawners.add(block);

        Bukkit.getScheduler().runTaskLater(SilkSpawners.getInstance(), () -> {
            Spawner newSpawner = new Spawner(block.getWorld().getBlockAt(block.getLocation()));

            if(!e.getPlayer().hasPermission("silkspawners.change." + spawner.getEntityType().getName()) && spawner.getEntityType() != newSpawner.getEntityType()) {
                spawner.setSpawnerBlockType(block);
                if(new ConfigValue<Boolean>(PluginConfig.SPAWNER_MESSAGE_DENY_CHANGE).get()) e.getPlayer().sendMessage(SilkSpawners.getInstance().getLocale().getMessage("SPAWNER_CHANGE_DENIED"));
            }

            Bukkit.getScheduler().runTaskLater(SilkSpawners.getInstance(), () -> editedSpawners.remove(block), 5);
        }, 5);
    }
}
