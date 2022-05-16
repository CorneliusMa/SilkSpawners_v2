package de.corneliusmay.silkspawners.api;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnerBreakEvent extends Event implements Cancellable {

    @Getter
    private final Player player;

    @Getter
    private final EntityType spawnedEntity;

    @Getter
    private final Block spawner;

    /**
     * This event is called, when a spawner is broken
     *
     * @param player The player who broke the spawner
     * @param spawnedEntity The entity spawned by the spawner
     * @param spawner The block which was broken
     */

    public SpawnerBreakEvent(Player player, EntityType spawnedEntity, Block spawner) {
        this.player = player;
        this.spawnedEntity = spawnedEntity;
        this.spawner = spawner;
    }

    private boolean cancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
