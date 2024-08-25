package de.corneliusmay.silkspawners.platform.bukkit;

import de.corneliusmay.silkspawners.api.ServerPlatform;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class PlatformImplementation extends ServerPlatform {
    public PlatformImplementation(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void runTaskLater(Location location, Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(this.plugin, runnable, delay);
    }
}
