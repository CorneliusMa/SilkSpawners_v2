package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Function;

/**
 * Called when breaking a spawner may drop it, before the drop chance is applied.
 * Cancelling prevents the drop, not the block break. If the spawner drops,
 * {@link SpawnerBreakEvent} is called afterwards.
 */
public class SpawnerDropEvent extends SpawnerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private ItemStack drop;

    private double dropChance;

    private boolean customDrop;

    @ApiStatus.Internal
    public SpawnerDropEvent(Player player, SpawnerSnapshot spawner, Location location, ItemStack drop, double dropChance, Function<EntityType, SpawnerSnapshot> snapshotFactory) {
        super(player, spawner, location, snapshotFactory);
        this.drop = drop;
        this.dropChance = dropChance;
    }

    /**
     * @return the item that will drop, initially the spawner item. In-place modifications
     * apply unless the spawner is replaced in the following {@link SpawnerBreakEvent}
     */
    public ItemStack getDrop() {
        return drop;
    }

    /**
     * Replaces the dropped item. Takes precedence over any spawner replacement made in the
     * following {@link SpawnerBreakEvent}.
     *
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
        if (!(dropChance >= 0 && dropChance <= 100)) throw new IllegalArgumentException("dropChance must be between 0 and 100");
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
