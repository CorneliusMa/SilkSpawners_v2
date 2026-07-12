package de.corneliusmay.silkspawners.platform.folia;

import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class PlatformImplementation extends ServerPlatform {
    public PlatformImplementation(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void runTaskLater(Location location, Runnable runnable, long delay) {
        this.plugin.getServer().getRegionScheduler().runDelayed(this.plugin, location, task -> runnable.run(), delay);
    }

    @Override
    public void runOnEntity(Entity entity, Runnable runnable, Runnable retired) {
        if(Bukkit.isOwnedByCurrentRegion(entity)) runnable.run();
        else if(entity.getScheduler().run(this.plugin, task -> runnable.run(), retired) == null) retired.run();
    }
}
