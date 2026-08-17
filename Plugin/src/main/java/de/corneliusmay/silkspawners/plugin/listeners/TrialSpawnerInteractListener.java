package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.api.events.TrialSpawnerChangeEvent;
import de.corneliusmay.silkspawners.plugin.entity.EntityNames;
import de.corneliusmay.silkspawners.plugin.spawner.EditedSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SpawnerTypeProfile;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawner;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawnerFactory;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.weftkit.wiring.Qualified;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TrialSpawnerInteractListener implements Listener {

    @Qualified("trialSpawner")
    private final SpawnerTypeProfile profile;

    private final TrialSpawnerFactory trialSpawnerFactory;

    private final DenyMessageHandler denyMessageHandler;

    private final EditedSpawners editedSpawners;

    private final ServerPlatform platform;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(PlayerInteractEvent e) {
        if (e.isCancelled() || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        EntityType entityType = trialSpawnerFactory.spawnEggEntityType(e.getItem());
        if (entityType == null) return;

        Block block = e.getClickedBlock();
        trialSpawnerFactory
                .fromBlock(block)
                .ifPresent(trialSpawner -> handleChange(e, block, trialSpawner, entityType));
    }

    private void handleChange(PlayerInteractEvent e, Block block, TrialSpawner trialSpawner, EntityType entityType) {
        Player p = e.getPlayer();
        if (!profile.canChange(p, EntityNames.serialized(entityType))) {
            denyMessageHandler.change(profile, p);
            return;
        }

        if (trialSpawner.getState().spawns(entityType)) return;

        Location location = block.getLocation();
        if (!editedSpawners.beginEdit(location)) return;

        TrialSpawnerState newState = trialSpawner.getState().withEntityType(entityType);
        TrialSpawnerChangeEvent event = new TrialSpawnerChangeEvent(p, location, trialSpawner.getState(), newState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            editedSpawners.endEdit(location);
            return;
        }

        // Now the plugin owns the interaction, so a change it does not carry out leaves the egg
        // its vanilla behaviour
        e.setCancelled(true);

        ItemStack egg = e.getItem();
        trialSpawnerFactory.applyToBlock(event.getNewState(), block, () -> consumeEgg(p, egg));
    }

    private void consumeEgg(Player player, ItemStack egg) {
        platform.runOnEntity(
                player,
                () -> {
                    if (player.getGameMode() == GameMode.CREATIVE) return;
                    egg.setAmount(egg.getAmount() - 1);
                },
                () -> {});
    }
}
