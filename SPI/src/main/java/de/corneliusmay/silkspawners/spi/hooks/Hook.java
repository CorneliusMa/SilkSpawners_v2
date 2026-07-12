package de.corneliusmay.silkspawners.spi.hooks;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Hook {

    protected final JavaPlugin plugin;

    protected final SpawnerProvider spawnerProvider;

    public Hook(JavaPlugin plugin, SpawnerProvider spawnerProvider) {
        this.plugin = plugin;
        this.spawnerProvider = spawnerProvider;
    }

    public abstract void register();
}
