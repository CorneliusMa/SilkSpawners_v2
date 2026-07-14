package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerChangeEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class PlayerInteractListener implements Listener {

    private final SpawnerFactory spawnerFactory;

    private final LocaleHandler locale;

    private final ServerPlatform platform;

    private final Set<Location> editedSpawners;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();

        spawnerFactory.fromBlock(block).ifPresent(spawner -> handleSpawnerInteract(e, block, spawner));
    }

    private void handleSpawnerInteract(PlayerInteractEvent e, Block block, Spawner spawner) {
        Location blockLocation = block.getLocation();
        if (!editedSpawners.add(blockLocation)) {
            e.setCancelled(true);
            return;
        }

        platform.runTaskLater(blockLocation, () -> handleSpawnerChange(e, block, blockLocation, spawner), 1);
    }

    private void handleSpawnerChange(PlayerInteractEvent e, Block block, Location blockLocation, Spawner spawner) {
        Optional<Spawner> changedSpawner =
                spawnerFactory.fromBlock(block.getWorld().getBlockAt(blockLocation));
        if (changedSpawner.isEmpty()) {
            editedSpawners.remove(blockLocation);
            return;
        }

        Spawner newSpawner = changedSpawner.get();

        if (spawner.getEntityType() == newSpawner.getEntityType()) {
            editedSpawners.remove(blockLocation);
            return;
        }

        if (!canChangeSpawner(e.getPlayer(), newSpawner)) {
            spawnerFactory.applyToBlock(spawner, block, this.editedSpawners);
            if (PluginConfig.SPAWNER_MESSAGE_DENY_CHANGE.get())
                e.getPlayer().sendMessage(locale.getMessage("SPAWNER_CHANGE_DENIED"));
            return;
        }

        SpawnerChangeEvent event =
                new SpawnerChangeEvent(e.getPlayer(), spawner, blockLocation, newSpawner, spawnerFactory::snapshot);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            spawnerFactory.applyToBlock(spawner, block, this.editedSpawners);
            return;
        }

        if (event.getNewSpawner() != newSpawner) {
            spawnerFactory.applyToBlock(spawnerFactory.of(event.getNewSpawner()), block, this.editedSpawners);
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
