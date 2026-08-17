package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.TrialSpawnerPlaceEvent;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SpawnerTypeProfile;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawner;
import de.corneliusmay.silkspawners.plugin.spawner.trial.TrialSpawnerFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.weftkit.wiring.Qualified;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TrialSpawnerPlaceListener implements Listener {

    @Qualified("trialSpawner")
    private final SpawnerTypeProfile profile;

    private final TrialSpawnerFactory trialSpawnerFactory;

    private final PlaceHandler placeHandler;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(BlockPlaceEvent e) {
        if (e.isCancelled()) return;

        trialSpawnerFactory.fromItem(e.getItemInHand()).ifPresent(trialSpawner -> handlePlace(e, trialSpawner));
    }

    private void handlePlace(BlockPlaceEvent e, TrialSpawner trialSpawner) {
        placeHandler.handle(
                e,
                profile,
                trialSpawner.serializedEntityType(),
                () -> new TrialSpawnerPlaceEvent(e.getPlayer(), e.getBlock().getLocation(), trialSpawner.getState()),
                event -> trialSpawnerFactory.placeAtBlock(event.getState(), e.getBlock()));
    }
}
