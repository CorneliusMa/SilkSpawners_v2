package de.corneliusmay.silkspawners.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

/**
 * One of the two configurations a trial spawner switches between, mirroring the Bukkit
 * {@code TrialSpawnerConfiguration} properties of the same names.
 *
 * <p>Only properties the Bukkit API exposes for both reading and writing are represented, so a
 * configuration always survives an item round trip unchanged. The potential spawns and the
 * display entity are not part of it and fall back to the vanilla defaults when a trial spawner
 * is placed again.
 *
 * @param entityType the spawned entity type, {@code null} for an empty spawner
 * @param delay delay between spawn attempts in ticks
 * @param requiredPlayerRange spawning requires a player within this range
 * @param spawnRange horizontal radius entities are spawned in
 * @param baseSpawnsBeforeCooldown entities spawned before the spawner cools down
 * @param baseSimultaneousEntities entities alive at the same time
 * @param additionalSpawnsBeforeCooldown additional spawns granted per extra player
 * @param additionalSimultaneousEntities additional simultaneous entities per extra player
 * @param possibleRewards loot tables the spawner picks its reward from, keyed by their
 *        namespaced key, mapped to their weight. {@code null} means the reward tables were never
 *        captured, in which case applying the configuration leaves whatever the block already has
 */
public record TrialSpawnerConfig(
        @Nullable EntityType entityType,
        int delay,
        int requiredPlayerRange,
        int spawnRange,
        float baseSpawnsBeforeCooldown,
        float baseSimultaneousEntities,
        float additionalSpawnsBeforeCooldown,
        float additionalSimultaneousEntities,
        @Nullable Map<String, Integer> possibleRewards) {

    public TrialSpawnerConfig {
        possibleRewards =
                possibleRewards == null ? null : Collections.unmodifiableMap(new LinkedHashMap<>(possibleRewards));
    }

    /**
     * @return whether the configuration has no entity type
     */
    public boolean isEmpty() {
        return entityType == null;
    }

    /**
     * @param entityType the new entity type, {@code null} for an empty spawner
     * @return a copy of this configuration with the given entity type
     */
    public TrialSpawnerConfig withEntityType(@Nullable EntityType entityType) {
        return new TrialSpawnerConfig(
                entityType,
                delay,
                requiredPlayerRange,
                spawnRange,
                baseSpawnsBeforeCooldown,
                baseSimultaneousEntities,
                additionalSpawnsBeforeCooldown,
                additionalSimultaneousEntities,
                possibleRewards);
    }
}
