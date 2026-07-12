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
 * Called when a spawner's entity type is changed with a spawn egg or {@code /silkspawners set}.
 * {@code getSpawner()} is the previous state. Cancelling keeps the previous entity type.
 */
public class SpawnerChangeEvent extends SpawnerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private SpawnerSnapshot newSpawner;

    @ApiStatus.Internal
    public SpawnerChangeEvent(Player player, SpawnerSnapshot spawner, Location location, SpawnerSnapshot newSpawner, Function<EntityType, SpawnerSnapshot> snapshotFactory) {
        super(player, spawner, location, snapshotFactory);
        this.newSpawner = newSpawner;
    }

    /**
     * @return the spawner state being applied
     */
    public SpawnerSnapshot getNewSpawner() {
        return newSpawner;
    }

    /**
     * Overrides the spawner state being applied.
     *
     * @param entityType the new entity type, {@code null} for an empty spawner
     * @throws IllegalArgumentException if the entity type is neither {@code null} nor spawnable
     */
    public void setNewSpawner(@Nullable EntityType entityType) {
        this.newSpawner = createSnapshot(entityType);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
