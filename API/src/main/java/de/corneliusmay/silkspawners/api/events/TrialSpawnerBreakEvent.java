package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;

/**
 * Called when a player breaks a trial spawner and it will drop.
 * Cancelling cancels the block break and nothing drops. The trial spawner stays placed.
 */
public class TrialSpawnerBreakEvent extends TrialSpawnerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    @ApiStatus.Internal
    public TrialSpawnerBreakEvent(Player player, Location location, TrialSpawnerState state) {
        super(player, location, state);
    }

    /**
     * Replaces the state the dropped item will carry.
     *
     * @param state the new state
     * @throws NullPointerException if the state is {@code null}, cancel the event instead
     */
    public void setState(TrialSpawnerState state) {
        replaceState(state);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
