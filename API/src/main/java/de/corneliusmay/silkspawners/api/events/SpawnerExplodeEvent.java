package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;

/**
 * Called before a spawner explosion.
 * Cancelling, or setting the power to zero or below, prevents the explosion.
 */
public class SpawnerExplodeEvent extends SpawnerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private float power;

    private boolean fire;

    private boolean breakBlocks;

    @ApiStatus.Internal
    public SpawnerExplodeEvent(
            Player player, SpawnerSnapshot spawner, Location location, float power, boolean fire, boolean breakBlocks) {
        super(player, spawner, location, null);
        this.power = power;
        this.fire = fire;
        this.breakBlocks = breakBlocks;
    }

    /**
     * @return the explosion power, where 4 equals TNT
     */
    public float getPower() {
        return power;
    }

    /**
     * @param power the explosion power, where 4 equals TNT and zero or below prevents the explosion
     * @throws IllegalArgumentException if the power is not finite
     */
    public void setPower(float power) {
        if (!Float.isFinite(power)) throw new IllegalArgumentException("power must be finite");
        this.power = power;
    }

    /**
     * @return whether the explosion sets fire
     */
    public boolean getFire() {
        return fire;
    }

    /**
     * @param fire whether the explosion sets fire
     */
    public void setFire(boolean fire) {
        this.fire = fire;
    }

    /**
     * @return whether the explosion breaks blocks
     */
    public boolean getBreakBlocks() {
        return breakBlocks;
    }

    /**
     * @param breakBlocks whether the explosion breaks blocks
     */
    public void setBreakBlocks(boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
