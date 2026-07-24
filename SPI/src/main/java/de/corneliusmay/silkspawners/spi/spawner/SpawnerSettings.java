package de.corneliusmay.silkspawners.spi.spawner;

public record SpawnerSettings(
        int minSpawnDelay,
        int maxSpawnDelay,
        int spawnCount,
        int maxNearbyEntities,
        int requiredPlayerRange,
        int spawnRange) {}
