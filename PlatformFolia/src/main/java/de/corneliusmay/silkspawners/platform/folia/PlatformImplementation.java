package de.corneliusmay.silkspawners.platform.folia;

import de.corneliusmay.silkspawners.api.ServerPlatform;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class PlatformImplementation extends ServerPlatform {
    public PlatformImplementation(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void runTaskLater(Location location, Runnable runnable, long delay) {
        this.plugin.getServer().getRegionScheduler().runDelayed(this.plugin, location, task -> runnable.run(), delay);
    }
}
