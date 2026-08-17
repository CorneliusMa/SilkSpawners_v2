package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.TrialSpawnerBreakEvent;
import de.corneliusmay.silkspawners.api.events.TrialSpawnerDropEvent;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SilkDropCheck;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SpawnerTypeProfile;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawner;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawnerFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.weftkit.wiring.Qualified;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TrialSpawnerBreakListener implements Listener {

    @Qualified("trialSpawner")
    private final SpawnerTypeProfile profile;

    private final TrialSpawnerFactory trialSpawnerFactory;

    @Qualified("trialSpawner")
    private final SilkDropCheck silkDropCheck;

    private final DenyMessageHandler denyMessageHandler;

    private final DropHandler dropHandler;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(BlockBreakEvent e) {
        if (e.isCancelled()) return;

        trialSpawnerFactory.fromBlock(e.getBlock()).ifPresent(trialSpawner -> handleBreak(e, trialSpawner));
    }

    private void handleBreak(BlockBreakEvent e, TrialSpawner trialSpawner) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) return;

        if (!silkDropCheck.canSilkDrop(p, trialSpawner.serializedEntityType())) {
            denyDestroy(p, e);
            return;
        }

        TrialSpawnerDropEvent dropEvent = new TrialSpawnerDropEvent(
                p,
                e.getBlock().getLocation(),
                trialSpawner.getState(),
                trialSpawner.getItemStack(),
                profile.dropChance().get());
        Bukkit.getPluginManager().callEvent(dropEvent);

        if (dropEvent.isCancelled()) return;

        if (dropHandler.missesDropChance(dropEvent.getDropChance())) {
            denyDestroy(p, e);
            return;
        }

        TrialSpawnerBreakEvent breakEvent =
                new TrialSpawnerBreakEvent(p, e.getBlock().getLocation(), dropEvent.getState());
        Bukkit.getPluginManager().callEvent(breakEvent);

        if (breakEvent.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        dropHandler.drop(e, drop(dropEvent, breakEvent), 0);
    }

    private ItemStack drop(TrialSpawnerDropEvent dropEvent, TrialSpawnerBreakEvent breakEvent) {
        if (dropEvent.hasCustomDrop() || !breakEvent.hasReplacedState()) return dropEvent.getDrop();
        return trialSpawnerFactory
                .of(breakEvent.getState())
                .map(TrialSpawner::getItemStack)
                .orElse(dropEvent.getDrop());
    }

    private void denyDestroy(Player p, BlockBreakEvent e) {
        if (profile.destroyable().get()) return;
        e.setCancelled(true);
        denyMessageHandler.destroy(profile, p);
    }
}
