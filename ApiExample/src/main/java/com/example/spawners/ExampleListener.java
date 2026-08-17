package com.example.spawners;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.api.events.SpawnerDropEvent;
import de.corneliusmay.silkspawners.api.events.SpawnerPlaceEvent;
import de.corneliusmay.silkspawners.api.events.TrialSpawnerBreakEvent;
import de.corneliusmay.silkspawners.api.events.TrialSpawnerChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ExampleListener implements Listener {

    @EventHandler
    public void onSpawnerDrop(SpawnerDropEvent event) {
        if (event.getPlayer().hasPermission("apiexample.vip")) {
            event.setDropChance(100);
        }
    }

    @EventHandler
    public void onSpawnerPlace(SpawnerPlaceEvent event) {
        SpawnerSnapshot spawner = event.getSpawner();
        String name = spawner.isEmpty() ? "empty" : spawner.getEntityType().getName();
        event.getPlayer().sendMessage("You placed a " + name + " spawner");
    }

    @EventHandler
    public void onTrialSpawnerBreak(TrialSpawnerBreakEvent event) {
        if (!event.getPlayer().hasPermission("apiexample.trial")) {
            event.setCancelled(true);
            return;
        }
        TrialSpawnerState state = event.getState();
        event.getPlayer()
                .sendMessage("You broke a trial spawner spawning "
                        + state.normal().entityType() + ", ominously "
                        + state.ominousConfig().entityType());
    }

    @EventHandler
    public void onTrialSpawnerChange(TrialSpawnerChangeEvent event) {
        event.getPlayer()
                .sendMessage("Trial spawner changed to "
                        + event.getNewState().active().entityType());
    }
}
