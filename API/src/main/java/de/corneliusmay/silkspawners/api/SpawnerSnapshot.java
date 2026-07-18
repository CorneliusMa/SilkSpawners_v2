package de.corneliusmay.silkspawners.api;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * View of a spawner as exposed by events and the API. A snapshot is not live: it reflects
 * the spawner's state at the time it was created.
 */
@ApiStatus.NonExtendable
public interface SpawnerSnapshot {

    /**
     * @return the spawner's entity type, {@code null} for an empty spawner
     */
    @Nullable
    EntityType getEntityType();

    /**
     * @return the display name of the spawner's entity type
     */
    String getDisplayName();

    /**
     * @return a copy of the spawner item representing this spawner, modifications
     * to the returned stack do not affect the spawner
     */
    ItemStack getItemStack();

    /**
     * @return whether the spawner has no entity type
     */
    boolean isEmpty();
}
