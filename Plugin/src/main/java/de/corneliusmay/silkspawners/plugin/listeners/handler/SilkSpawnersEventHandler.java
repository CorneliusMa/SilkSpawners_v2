package de.corneliusmay.silkspawners.plugin.listeners.handler;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class SilkSpawnersEventHandler {

    private final SilkSpawners plugin;

    private final PluginManager pluginManager;

    public SilkSpawnersEventHandler(SilkSpawners plugin) {
        this.plugin = plugin;
        this.pluginManager = Bukkit.getPluginManager();
    }

    public void registerListener(SilkSpawnersListener<?> listener) {
        listener.setPlugin(plugin);
        pluginManager.registerEvents(listener, plugin);
    }
}
