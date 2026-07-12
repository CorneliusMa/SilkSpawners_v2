package de.corneliusmay.silkspawners.api.events;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;

/**
 * Called when {@code /silkspawners give} hands out spawners.
 * {@code getPlayer()} is the receiving player. Cancelling silently prevents the give.
 */
public class SpawnerGiveEvent extends SpawnerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final CommandSender sender;

    private int amount;

    @ApiStatus.Internal
    public SpawnerGiveEvent(CommandSender sender, Player receiver, SpawnerSnapshot spawner, int amount) {
        super(receiver, spawner, receiver.getLocation(), null);
        this.sender = sender;
        this.amount = amount;
    }

    /**
     * @return who issued the command, possibly the receiver itself or the console
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * @return the amount of spawner items to give, at least 1
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount of spawner items to give, at least 1
     * @throws IllegalArgumentException if the amount is smaller than 1
     */
    public void setAmount(int amount) {
        if (amount < 1) throw new IllegalArgumentException("amount must be at least 1");
        this.amount = amount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
