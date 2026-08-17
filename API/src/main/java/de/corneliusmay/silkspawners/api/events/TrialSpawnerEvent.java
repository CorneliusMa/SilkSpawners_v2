package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * Base of all SilkSpawners trial spawner events. Trial spawners carry a state model of their
 * own, so these events form a hierarchy separate from {@link SpawnerEvent}.
 *
 * <p>Events are fired synchronously on the main server thread, on Folia on the thread owning
 * the spawner's region. They are meant to be listened to, not fired by other plugins.
 */
public abstract class TrialSpawnerEvent extends Event implements Cancellable {

    private final Player player;

    private final Location location;

    private TrialSpawnerState state;

    private boolean stateReplaced;

    private boolean cancelled;

    @ApiStatus.Internal
    protected TrialSpawnerEvent(Player player, Location location, TrialSpawnerState state) {
        this.player = player;
        this.location = location;
        this.state = state;
    }

    /**
     * @return the player who triggered the event
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the trial spawner's location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return the trial spawner's state
     */
    public TrialSpawnerState getState() {
        return state;
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

    protected void replaceState(TrialSpawnerState state) {
        this.state = Objects.requireNonNull(state, "state");
        this.stateReplaced = true;
    }

    @ApiStatus.Internal
    public boolean hasReplacedState() {
        return stateReplaced;
    }
}
