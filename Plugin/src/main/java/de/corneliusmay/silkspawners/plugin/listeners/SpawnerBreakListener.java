package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.explosion.Explosion;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.wiring.Wired;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Wired
@RequiredArgsConstructor
public class SpawnerBreakListener implements Listener {

    private final ServerPlatform platform;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(SpawnerBreakEvent e) {
        if (e.isCancelled()) return;
        Explosion explosion = new Explosion(PluginConfig.SPAWNER_EXPLOSION_SILKTOUCH);
        if (!explosion.applies(e.getPlayer())) return;
        platform.runTaskLater(
                e.getLocation(),
                () -> {
                    if (e.isCancelled()) return;
                    explosion.run(e.getPlayer(), e.getLocation().getWorld(), e.getLocation(), e.getSpawner());
                },
                1);
    }
}
