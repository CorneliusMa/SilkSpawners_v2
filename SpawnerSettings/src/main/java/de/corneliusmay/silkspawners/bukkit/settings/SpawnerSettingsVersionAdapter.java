package de.corneliusmay.silkspawners.bukkit.settings;

import de.corneliusmay.silkspawners.api.SpawnerSettings;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import org.bukkit.block.CreatureSpawner;

public interface SpawnerSettingsVersionAdapter extends VersionAdapter {

    @Override
    default SpawnerSettings readSpawnerSettings(CreatureSpawner spawner) {
        return new SpawnerSettings(
                spawner.getMinSpawnDelay(),
                spawner.getMaxSpawnDelay(),
                spawner.getSpawnCount(),
                spawner.getMaxNearbyEntities(),
                spawner.getRequiredPlayerRange(),
                spawner.getSpawnRange());
    }

    @Override
    default void applySpawnerSettings(CreatureSpawner spawner, SpawnerSettings settings) {
        // Spigot rejects min/max delays that cross each other, so keep every intermediate state valid
        spawner.setMinSpawnDelay(0);
        spawner.setMaxSpawnDelay(settings.maxSpawnDelay());
        spawner.setMinSpawnDelay(settings.minSpawnDelay());
        spawner.setSpawnCount(settings.spawnCount());
        spawner.setMaxNearbyEntities(settings.maxNearbyEntities());
        spawner.setRequiredPlayerRange(settings.requiredPlayerRange());
        spawner.setSpawnRange(settings.spawnRange());
    }
}
