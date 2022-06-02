package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.events.SpawnerBreakEvent;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.Explosion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SpawnerBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawnerBreak(SpawnerBreakEvent e) {
        new Explosion(e.getPlayer(), e.getLocation().getWorld(), e.getLocation(), new ConfigValue<Integer>(PluginConfig.SPAWNER_EXPLOSION_SILKTOUCH).get());
    }
}
