package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;

public class SpawnerLoader {

    private final SilkSpawners plugin;

    public SpawnerLoader(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    public void load() {
        SpawnerContext.commit(plugin.getBukkitHandler(), plugin.getPlatform());
    }
}
