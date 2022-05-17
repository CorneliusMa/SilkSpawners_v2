package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.utils.Explosion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SpawnerBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawnerBreak(SpawnerBreakEvent e) {
        new Explosion(e.getSpawner().getWorld(), e.getSpawner().getLocation(), SilkSpawners.getInstance().getPluginConfig().getSpawnerExplosionSilktouch());
    }
}
