package com.example.spawners;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.api.events.SpawnerDropEvent;
import de.corneliusmay.silkspawners.api.events.SpawnerPlaceEvent;
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
}
