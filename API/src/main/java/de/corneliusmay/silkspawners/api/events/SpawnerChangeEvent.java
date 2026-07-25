package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.SpawnerSettings;
import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import java.util.function.BiFunction;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a spawner's entity type is changed with a spawn egg or {@code /silkspawners set}.
 * {@code getSpawner()} is the previous state. Cancelling keeps the previous entity type.
 */
public class SpawnerChangeEvent extends SpawnerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private SpawnerSnapshot newSpawner;

    @ApiStatus.Internal
    public SpawnerChangeEvent(
            Player player,
            SpawnerSnapshot spawner,
            Location location,
            SpawnerSnapshot newSpawner,
            BiFunction<EntityType, SpawnerSettings, SpawnerSnapshot> snapshotFactory) {
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
     * Overrides the spawner state being applied. The override keeps the previous spawner's block settings.
     *
     * @param entityType the new entity type, {@code null} for an empty spawner
     * @throws IllegalArgumentException if the entity type is neither {@code null} nor spawnable
     */
    public void setNewSpawner(@Nullable EntityType entityType) {
        this.newSpawner = createSnapshot(entityType, getSpawner().getSettings());
    }

    /**
     * Overrides the spawner state being applied with the given entity type and block settings.
     *
     * @param entityType the new entity type, {@code null} for an empty spawner
     * @param settings the block settings, {@code null} for the vanilla defaults
     * @throws IllegalArgumentException if the entity type is neither {@code null} nor spawnable,
     *         or the settings are invalid
     */
    public void setNewSpawner(@Nullable EntityType entityType, @Nullable SpawnerSettings settings) {
        this.newSpawner = createSnapshot(entityType, settings);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
