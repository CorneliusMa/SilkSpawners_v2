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
 * <p>The remaining cooldown is not exposed by the Bukkit API and can therefore neither be read
 * nor restored. {@link #cooldownPending()} records whether the spawner was ejecting its reward
 * or cooling down when it was broken, which SilkSpawners uses to re-apply a cooldown after the
 * spawner is placed again.
 *
 * @param ominous whether the ominous configuration is currently in use
 * @param cooldownLength the configured cooldown length in ticks
 * @param cooldownPending whether the spawner still owed a cooldown when it was broken
 * @param normal the configuration used while not ominous
 * @param ominousConfig the configuration used while ominous
 */
public record TrialSpawnerState(
        boolean ominous,
        int cooldownLength,
        boolean cooldownPending,
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
                cooldownPending,
                normal.withEntityType(entityType),
                ominousConfig.withEntityType(entityType));
    }

    /**
     * @param cooldownPending whether a cooldown is owed after the spawner is placed again
     * @return a copy of this state with the given cooldown flag
     */
    public TrialSpawnerState withCooldownPending(boolean cooldownPending) {
        return new TrialSpawnerState(ominous, cooldownLength, cooldownPending, normal, ominousConfig);
    }
}
