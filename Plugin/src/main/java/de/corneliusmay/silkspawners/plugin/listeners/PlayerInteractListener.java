package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerChangeEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.spawner.EditedSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class PlayerInteractListener implements Listener {

    private final SpawnerFactory spawnerFactory;

    private final LocaleHandler locale;

    private final ServerPlatform platform;

    private final EditedSpawners editedSpawners;

    private final VersionAdapter versionAdapter;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();

        spawnerFactory.fromBlock(block).ifPresent(spawner -> handleSpawnerInteract(e, block, spawner));
    }

    private void handleSpawnerInteract(PlayerInteractEvent e, Block block, Spawner spawner) {
        Location blockLocation = block.getLocation();
        if (!editedSpawners.beginEdit(blockLocation)) {
            e.setCancelled(true);
            return;
        }

        ItemStack usedEgg = findHeldSpawnEgg(e.getPlayer());
        if (handledPreemptively(e, spawner, usedEgg)) {
            editedSpawners.endEdit(blockLocation);
            return;
        }
        platform.runTaskLater(blockLocation, () -> handleSpawnerChange(e, block, blockLocation, spawner, usedEgg), 1);
    }

    private void handleSpawnerChange(
            PlayerInteractEvent e, Block block, Location blockLocation, Spawner spawner, ItemStack usedEgg) {
        Optional<Spawner> changedSpawner =
                spawnerFactory.fromBlock(block.getWorld().getBlockAt(blockLocation));
        if (changedSpawner.isEmpty()) {
            editedSpawners.endEdit(blockLocation);
            return;
        }

        Spawner newSpawner = changedSpawner.get();
        Player player = e.getPlayer();

        if (spawner.getEntityType() == newSpawner.getEntityType()) {
            editedSpawners.endEdit(blockLocation);
            return;
        }

        if (!canChangeSpawner(player, newSpawner.getEntityType())) {
            revertChange(player, spawner, block, usedEgg);
            if (PluginConfig.SPAWNER_MESSAGE_DENY_CHANGE.get())
                player.sendMessage(locale.getMessage("SPAWNER_CHANGE_DENIED"));
            return;
        }

        SpawnerChangeEvent event =
                new SpawnerChangeEvent(player, spawner, blockLocation, newSpawner, spawnerFactory::snapshot);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            revertChange(player, spawner, block, usedEgg);
            return;
        }

        if (event.getNewSpawner() != newSpawner) {
            spawnerFactory.applyToBlock(spawnerFactory.of(event.getNewSpawner()), block);
            return;
        }

        editedSpawners.endEdit(blockLocation);
    }

    private void revertChange(Player player, Spawner original, Block block, ItemStack usedEgg) {
        spawnerFactory.applyToBlock(original, block);
        refundSpawnEgg(player, usedEgg);
    }

    private void refundSpawnEgg(Player player, ItemStack usedEgg) {
        if (usedEgg == null || player.getGameMode() == GameMode.CREATIVE) return;

        player.getInventory()
                .addItem(usedEgg)
                .values()
                .forEach(overflow -> player.getWorld().dropItemNaturally(player.getLocation(), overflow));
    }

    private ItemStack findHeldSpawnEgg(Player player) {
        for (ItemStack item : versionAdapter.getItemsInHand(player)) {
            if (item == null || !versionAdapter.isSpawnEgg(item)) continue;
            ItemStack egg = item.clone();
            egg.setAmount(1);
            return egg;
        }
        return null;
    }

    private boolean handledPreemptively(PlayerInteractEvent e, Spawner spawner, ItemStack usedEgg) {
        if (usedEgg == null || e.useItemInHand() == Event.Result.DENY) return false;
        EntityType target = versionAdapter.spawnEggEntityType(usedEgg);
        if (target == null) return false;
        if (target == spawner.getEntityType()) {
            e.setCancelled(true);
            return true;
        }
        if (canChangeSpawner(e.getPlayer(), target)) return false;

        e.setCancelled(true);
        if (PluginConfig.SPAWNER_MESSAGE_DENY_CHANGE.get())
            e.getPlayer().sendMessage(locale.getMessage("SPAWNER_CHANGE_DENIED"));
        return true;
    }

    private boolean canChangeSpawner(Player player, EntityType entityType) {
        return player.hasPermission("silkspawners.change." + Spawner.serializedEntityType(entityType))
                || player.hasPermission("silkspawners.change.*")
                || PluginConfig.SPAWNER_PERMISSION_DISABLE_CHANGE.get();
    }
}
