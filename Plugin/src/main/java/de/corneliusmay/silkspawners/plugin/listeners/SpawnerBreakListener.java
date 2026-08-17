package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.api.events.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.explosion.Explosion;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class SpawnerBreakListener implements Listener {

    private final PluginConfig config;

    private final ServerPlatform platform;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCall(SpawnerBreakEvent e) {
        if (e.isCancelled()) return;
        Explosion explosion = new Explosion(config.SPAWNER_EXPLOSION_SILKTOUCH, config.SPAWNER_EXPLOSION_ALL);
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
