package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * Called when breaking a trial spawner may drop it, before the drop chance is applied.
 * Cancelling prevents the drop, not the block break. If the trial spawner drops,
 * {@link TrialSpawnerBreakEvent} is called afterwards.
 */
public class TrialSpawnerDropEvent extends TrialSpawnerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private ItemStack drop;

    private double dropChance;

    private boolean customDrop;

    @ApiStatus.Internal
    public TrialSpawnerDropEvent(
            Player player, Location location, TrialSpawnerState state, ItemStack drop, double dropChance) {
        super(player, location, state);
        this.drop = drop;
        this.dropChance = dropChance;
    }

    /**
     * @return the item that will drop, initially the trial spawner item. In-place modifications
     * apply unless the state is replaced in the following {@link TrialSpawnerBreakEvent}
     */
    public ItemStack getDrop() {
        return drop;
    }

    /**
     * Replaces the dropped item. Takes precedence over any state replacement made in the
     * following {@link TrialSpawnerBreakEvent}.
     *
     * @param drop the item to drop
     * @throws NullPointerException if the drop is {@code null}, cancel the event instead
     */
    public void setDrop(ItemStack drop) {
        this.drop = Objects.requireNonNull(drop, "drop");
        this.customDrop = true;
    }

    /**
     * @return the drop chance in percent (0-100), initially the configured value
     */
    public double getDropChance() {
        return dropChance;
    }

    /**
     * @param dropChance the drop chance in percent (0-100)
     * @throws IllegalArgumentException if the drop chance is not between 0 and 100
     */
    public void setDropChance(double dropChance) {
        if (!(dropChance >= 0 && dropChance <= 100))
            throw new IllegalArgumentException("dropChance must be between 0 and 100");
        this.dropChance = dropChance;
    }

    @ApiStatus.Internal
    public boolean hasCustomDrop() {
        return customDrop;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
