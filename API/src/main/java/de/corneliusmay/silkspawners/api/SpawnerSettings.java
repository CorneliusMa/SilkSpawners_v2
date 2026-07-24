package de.corneliusmay.silkspawners.api;

/**
 * The tunable properties of a spawner block, mirroring the vanilla spawner fields of the
 * same names. Delays are measured in ticks.
 *
 * <p>Spawner items only carry non-default settings: wherever the API exposes this type,
 * {@code null} means the spawner uses the vanilla defaults.
 *
 * <p>All values must be non-negative, {@code maxSpawnDelay} must be positive and
 * {@code minSpawnDelay} must not exceed it. API methods accepting settings reject
 * violations with an {@link IllegalArgumentException}.
 *
 * @param minSpawnDelay minimum delay between spawn attempts
 * @param maxSpawnDelay maximum delay between spawn attempts
 * @param spawnCount number of entities spawned per attempt
 * @param maxNearbyEntities spawning pauses while this many entities are nearby
 * @param requiredPlayerRange spawning requires a player within this range
 * @param spawnRange horizontal radius entities are spawned in
 */
public record SpawnerSettings(
        int minSpawnDelay,
        int maxSpawnDelay,
        int spawnCount,
        int maxNearbyEntities,
        int requiredPlayerRange,
        int spawnRange) {}
