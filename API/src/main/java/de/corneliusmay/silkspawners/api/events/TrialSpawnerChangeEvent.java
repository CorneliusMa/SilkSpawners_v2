package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;

/**
 * Called when a trial spawner's entity type is changed with a spawn egg or
 * {@code /silkspawners set}. {@code getState()} is the previous state, {@link #getNewState()}
 * the one being applied. Cancelling keeps the previous state.
 *
 * <p>The incoming state applies the new entity type to both configurations, so the spawner
 * does not spawn different entities depending on whether it is ominous.
 */
public class TrialSpawnerChangeEvent extends TrialSpawnerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private TrialSpawnerState newState;

    @ApiStatus.Internal
    public TrialSpawnerChangeEvent(
            Player player, Location location, TrialSpawnerState state, TrialSpawnerState newState) {
        super(player, location, state);
        this.newState = newState;
    }

    /**
     * @return the state being applied
     */
    public TrialSpawnerState getNewState() {
        return newState;
    }

    /**
     * Overrides the state being applied.
     *
     * @param newState the state to apply
     * @throws NullPointerException if the state is {@code null}, cancel the event instead
     */
    public void setNewState(TrialSpawnerState newState) {
        this.newState = Objects.requireNonNull(newState, "newState");
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
