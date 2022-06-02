package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.events.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.utils.Explosion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class SpawnerBreakListener extends SilkSpawnersListener<SpawnerBreakEvent> {

    @Override @EventHandler(priority = EventPriority.HIGHEST)
    protected void onCall(SpawnerBreakEvent e) {
        new Explosion(e.getPlayer(), e.getLocation().getWorld(), e.getLocation(), new ConfigValue<Integer>(PluginConfig.SPAWNER_EXPLOSION_SILKTOUCH).get());

    }
}
