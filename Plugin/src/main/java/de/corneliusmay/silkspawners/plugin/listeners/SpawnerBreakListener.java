package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.events.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.explosion.Explosion;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTier;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class SpawnerBreakListener extends SilkSpawnersListener<SpawnerBreakEvent> {

    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(SpawnerBreakEvent e) {
        plugin.getPlatform().runTaskLater(e.getLocation(), () -> {
            new Explosion(e.getPlayer(), e.getLocation().getWorld(), e.getLocation(), new ConfigValue<List<ExplosionTier>>(PluginConfig.SPAWNER_EXPLOSION_SILKTOUCH).get());
        }, 1);
    }
}
