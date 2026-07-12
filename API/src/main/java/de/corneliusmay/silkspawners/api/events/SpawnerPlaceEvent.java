package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Called when a player places a spawner.
 * Cancelling also cancels the block place.
 */
public class SpawnerPlaceEvent extends SpawnerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    @ApiStatus.Internal
    public SpawnerPlaceEvent(Player player, SpawnerSnapshot spawner, Location location, Function<EntityType, SpawnerSnapshot> snapshotFactory) {
        super(player, spawner, location, snapshotFactory);
    }

    /**
     * Replaces the placed spawner.
     *
     * @param entityType the new entity type, {@code null} for an empty spawner
     * @throws IllegalArgumentException if the entity type is neither {@code null} nor spawnable
     */
    public void setSpawner(@Nullable EntityType entityType) {
        replaceSpawner(entityType);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
