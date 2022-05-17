package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpawnerBreakListener implements Listener {

    @EventHandler
    public void onSpawnerBreak(SpawnerBreakEvent e) {
        int intensity = SilkSpawners.getInstance().getPluginConfig().getSpawnerExplosionSilktouch();
        if(intensity == 0) return;
        e.getSpawner().getWorld().createExplosion(e.getSpawner().getLocation(), intensity);
    }
}
