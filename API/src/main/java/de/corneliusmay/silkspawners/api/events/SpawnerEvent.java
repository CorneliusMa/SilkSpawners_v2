package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * Base of all SilkSpawners events. Events are fired synchronously on the main server thread,
 * on Folia on the thread owning the spawner's region. They are meant to be listened to,
 * not fired by other plugins.
 */
public abstract class SpawnerEvent extends Event implements Cancellable {

    private final Player player;

    private final Location location;

    private final Function<EntityType, SpawnerSnapshot> snapshotFactory;

    private SpawnerSnapshot spawner;

    private boolean spawnerReplaced;

    private boolean cancelled;

    @ApiStatus.Internal
    protected SpawnerEvent(
            Player player,
            SpawnerSnapshot spawner,
            Location location,
            Function<EntityType, SpawnerSnapshot> snapshotFactory) {
        this.player = player;
        this.spawner = spawner;
        this.location = location;
        this.snapshotFactory = snapshotFactory;
    }

    /**
     * @return the player who triggered the event
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the spawner's location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return the spawner involved
     */
    public SpawnerSnapshot getSpawner() {
        return spawner;
    }

    /**
     * @return the cancelled state
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancelled state. The effect of cancelling is defined by the concrete event.
     *
     * @param cancelled the cancelled state
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    protected void replaceSpawner(EntityType entityType) {
        this.spawner = createSnapshot(entityType);
        this.spawnerReplaced = true;
    }

    @ApiStatus.Internal
    public boolean hasReplacedSpawner() {
        return spawnerReplaced;
    }

    protected SpawnerSnapshot createSnapshot(EntityType entityType) {
        if (entityType != null && !entityType.isSpawnable())
            throw new IllegalArgumentException("Entity type " + entityType + " is not spawnable");
        return snapshotFactory.apply(entityType);
    }
}
