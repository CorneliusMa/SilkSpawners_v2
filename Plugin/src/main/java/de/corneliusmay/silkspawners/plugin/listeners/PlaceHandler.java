package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.spawner.EditedSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SpawnerTypeProfile;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
class PlaceHandler {

    private final DenyMessageHandler denyMessageHandler;

    private final EditedSpawners editedSpawners;

    <E extends Event & Cancellable> void handle(
            BlockPlaceEvent e, SpawnerTypeProfile profile, String entity, Supplier<E> event, Consumer<E> apply) {
        Player p = e.getPlayer();
        if (!profile.canPlace(p, entity)) {
            e.setCancelled(true);
            denyMessageHandler.place(profile, p);
            return;
        }

        E placeEvent = event.get();
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        if (!editedSpawners.beginEdit(e.getBlock().getLocation())) {
            e.setCancelled(true);
            return;
        }
        apply.accept(placeEvent);
    }
}
