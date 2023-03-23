package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.entity.EntityType;

public class SpawnerAPI {
    
    private static SilkSpawners plugin;
    
    public SpawnerAPI(SilkSpawners instance) {
        plugin = instance;
    }
    
    public static Spawner get(EntityType entityType) {
        Spawner spawner = new Spawner(plugin, entityType);
        if(spawner.isValid()) return spawner;

        plugin.getLog().warn("The following error is caused by a plugin using SilkSpawners as API.");
        throw new IllegalArgumentException("Entity type " + entityType.getName() + " is not valid.");
    }
}
