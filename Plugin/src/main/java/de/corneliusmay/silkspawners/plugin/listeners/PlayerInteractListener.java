package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerChangeEvent;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public class PlayerInteractListener extends SilkSpawnersListener<PlayerInteractEvent> {

    private final Set<Location> editedSpawners;

    public PlayerInteractListener(Set<Location> editedSpawners) {
        this.editedSpawners = editedSpawners;
    }

    @Override @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();

        Location blockLocation = block.getLocation();
        Spawner spawner = new Spawner(plugin, block);
        if(!spawner.isValid()) return;

        if(!editedSpawners.add(blockLocation)) {
            e.setCancelled(true);
            return;
        }

        this.plugin.getPlatform().runTaskLater(blockLocation, () -> {
            Spawner newSpawner = new Spawner(plugin, block.getWorld().getBlockAt(blockLocation));

            if(!newSpawner.isValid()) {
                editedSpawners.remove(blockLocation);
                return;
            }

            if(spawner.getEntityType() == newSpawner.getEntityType()) {
                editedSpawners.remove(blockLocation);
                return;
            }

            if(!e.getPlayer().hasPermission("silkspawners.change." + newSpawner.serializedEntityType())
                    && !e.getPlayer().hasPermission("silkspawners.change.*")
                    && !new ConfigValue<Boolean>(PluginConfig.SPAWNER_PERMISSION_DISABLE_CHANGE).get()) {
                spawner.setSpawnerBlockType(block, this.editedSpawners);
                if(new ConfigValue<Boolean>(PluginConfig.SPAWNER_MESSAGE_DENY_CHANGE).get()) e.getPlayer().sendMessage(plugin.getLocale().getMessage("SPAWNER_CHANGE_DENIED"));
                return;
            }

            SpawnerChangeEvent event = new SpawnerChangeEvent(e.getPlayer(), spawner, blockLocation, newSpawner, type -> new Spawner(plugin, type));
            Bukkit.getPluginManager().callEvent(event);

            if(event.isCancelled()) {
                spawner.setSpawnerBlockType(block, this.editedSpawners);
                return;
            }

            if(event.getNewSpawner() != newSpawner) {
                Spawner.of(plugin, event.getNewSpawner()).setSpawnerBlockType(block, this.editedSpawners);
                return;
            }

            editedSpawners.remove(blockLocation);
        }, 1);
    }
}
