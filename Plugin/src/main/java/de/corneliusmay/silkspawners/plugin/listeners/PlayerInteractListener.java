package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerChangeEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener extends SilkSpawnersListener<PlayerInteractEvent> {

    private final Set<Location> editedSpawners;

    public PlayerInteractListener(Set<Location> editedSpawners) {
        this.editedSpawners = editedSpawners;
    }

    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();

        Location blockLocation = block.getLocation();
        Spawner spawner = new Spawner(plugin, block);
        if (!spawner.isValid()) return;

        if (!editedSpawners.add(blockLocation)) {
            e.setCancelled(true);
            return;
        }

        this.plugin
                .getPlatform()
                .runTaskLater(blockLocation, () -> handleSpawnerChange(e, block, blockLocation, spawner), 1);
    }

    private void handleSpawnerChange(PlayerInteractEvent e, Block block, Location blockLocation, Spawner spawner) {
        Spawner newSpawner = new Spawner(plugin, block.getWorld().getBlockAt(blockLocation));

        if (!newSpawner.isValid()) {
            editedSpawners.remove(blockLocation);
            return;
        }

        if (spawner.getEntityType() == newSpawner.getEntityType()) {
            editedSpawners.remove(blockLocation);
            return;
        }

        if (!canChangeSpawner(e.getPlayer(), newSpawner)) {
            spawner.setSpawnerBlockType(block, this.editedSpawners);
            if (PluginConfig.SPAWNER_MESSAGE_DENY_CHANGE.get())
                e.getPlayer().sendMessage(plugin.getLocale().getMessage("SPAWNER_CHANGE_DENIED"));
            return;
        }

        SpawnerChangeEvent event = new SpawnerChangeEvent(
                e.getPlayer(), spawner, blockLocation, newSpawner, type -> new Spawner(plugin, type));
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            spawner.setSpawnerBlockType(block, this.editedSpawners);
            return;
        }

        if (event.getNewSpawner() != newSpawner) {
            Spawner.of(plugin, event.getNewSpawner()).setSpawnerBlockType(block, this.editedSpawners);
            return;
        }

        editedSpawners.remove(blockLocation);
    }

    private boolean canChangeSpawner(Player player, Spawner newSpawner) {
        return player.hasPermission("silkspawners.change." + newSpawner.serializedEntityType())
                || player.hasPermission("silkspawners.change.*")
                || PluginConfig.SPAWNER_PERMISSION_DISABLE_CHANGE.get();
    }
}
