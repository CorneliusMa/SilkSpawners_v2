package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.explosion.Explosion;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class SpawnerBreakListener extends SilkSpawnersListener<SpawnerBreakEvent> {

    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(SpawnerBreakEvent e) {
        if (e.isCancelled()) return;
        Explosion explosion = new Explosion(PluginConfig.SPAWNER_EXPLOSION_SILKTOUCH);
        if (!explosion.applies(e.getPlayer())) return;
        plugin.getPlatform()
                .runTaskLater(
                        e.getLocation(),
                        () -> {
                            if (e.isCancelled()) return;
                            explosion.run(e.getPlayer(), e.getLocation().getWorld(), e.getLocation(), e.getSpawner());
                        },
                        1);
    }
}
