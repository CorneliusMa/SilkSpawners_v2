package de.corneliusmay.silkspawners.api;

import java.util.Objects;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

/**
 * The state of a trial spawner, mirroring the Bukkit {@code TrialSpawner} block properties.
 *
 * <p>A trial spawner holds two configurations and switches between them depending on whether
 * it is ominous. Both are kept so that breaking and placing a trial spawner preserves it
 * completely.
 *
 * <p>The remaining reward cooldown is carried in ticks, so breaking and placing a trial
 * spawner resumes the cooldown instead of resetting it.
 *
 * @param ominous whether the ominous configuration is currently in use
 * @param cooldownLength the configured cooldown length in ticks
 * @param cooldownRemaining the remaining cooldown in ticks when the spawner was broken,
 *     {@code 0} if it was ready
 * @param normal the configuration used while not ominous
 * @param ominousConfig the configuration used while ominous
 */
public record TrialSpawnerState(
        boolean ominous,
        int cooldownLength,
        int cooldownRemaining,
        TrialSpawnerConfig normal,
        TrialSpawnerConfig ominousConfig) {

    public TrialSpawnerState {
        Objects.requireNonNull(normal, "normal");
        Objects.requireNonNull(ominousConfig, "ominousConfig");
    }

    /**
     * @return the configuration the spawner currently uses
     */
    public TrialSpawnerConfig active() {
        return ominous ? ominousConfig : normal;
    }

    /**
     * @param entityType the entity type to test for, {@code null} for an empty spawner
     * @return whether both configurations spawn the given entity type
     */
    public boolean spawns(@Nullable EntityType entityType) {
        return normal.entityType() == entityType && ominousConfig.entityType() == entityType;
    }

    /**
     * Both configurations receive the entity type, so the spawner does not spawn something else
     * once it turns ominous.
     *
     * @param entityType the new entity type, {@code null} for an empty spawner
     * @return a copy of this state with the given entity type
     */
    public TrialSpawnerState withEntityType(@Nullable EntityType entityType) {
        return new TrialSpawnerState(
                ominous,
                cooldownLength,
                cooldownRemaining,
                normal.withEntityType(entityType),
                ominousConfig.withEntityType(entityType));
    }

    /**
     * @param cooldownRemaining the cooldown in ticks the spawner resumes after it is placed again
     * @return a copy of this state with the given remaining cooldown
     */
    public TrialSpawnerState withCooldownRemaining(int cooldownRemaining) {
        return new TrialSpawnerState(ominous, cooldownLength, cooldownRemaining, normal, ominousConfig);
    }
}
