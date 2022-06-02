package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.event.Listener;

public abstract class SilkSpawnersListener implements Listener {

    protected final SilkSpawners plugin;

    public SilkSpawnersListener(SilkSpawners plugin) {
        this.plugin = plugin;
    }
}
