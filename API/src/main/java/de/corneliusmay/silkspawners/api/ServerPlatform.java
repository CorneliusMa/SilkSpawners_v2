package de.corneliusmay.silkspawners.api;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ServerPlatform {
    protected final JavaPlugin plugin;

    public ServerPlatform(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void runTaskLater(Location location, Runnable runnable, long delay);
}
