package de.corneliusmay.silkspawners.plugin.events;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnerPlaceEvent extends Event implements Cancellable {

    @Getter
    private final Player player;

    @Getter
    private Spawner spawner;

    @Getter
    private final Location location;

    private final SilkSpawners plugin;

    /**
     * This event is called, when a spawner is placed
     *
     * @param player   The player who placed the spawner
     * @param spawner  The spawner
     * @param location The spawner location
     * @param plugin   The plugin instance
     */

    public SpawnerPlaceEvent(Player player, Spawner spawner, Location location, SilkSpawners plugin) {
        this.player = player;
        this.spawner = spawner;
        this.location = location;
        this.plugin = plugin;
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

    public void setSpawner(EntityType entityType) {
        this.spawner = new Spawner(plugin, entityType);
    }
}
