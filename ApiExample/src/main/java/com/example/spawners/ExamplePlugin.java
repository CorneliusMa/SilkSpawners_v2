package com.example.spawners;

import de.corneliusmay.silkspawners.api.SilkSpawnersAPI;
import de.corneliusmay.silkspawners.api.SilkSpawnersApiProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SilkSpawnersAPI api = SilkSpawnersApiProvider.get();
        getLogger().info("SilkSpawners supports " + api.getSupportedEntityTypes().size() + " entity types");
        getServer().getPluginManager().registerEvents(new ExampleListener(), this);
    }
}
