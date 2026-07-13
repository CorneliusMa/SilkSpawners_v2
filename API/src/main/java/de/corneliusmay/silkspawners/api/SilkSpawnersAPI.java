package de.corneliusmay.silkspawners.api;

import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Service interface of SilkSpawners. Obtain it via {@link SilkSpawnersApiProvider#get()}.
 *
 * <p>All methods must be called from the main server thread, on Folia from the thread owning
 * the affected region. Block changes are safe on Folia.
 */
@ApiStatus.NonExtendable
public interface SilkSpawnersAPI {

    /**
     * Builds the spawner item for the given entity type, using the configured name and lore.
     *
     * @param entityType the spawner's entity type, {@code null} for an empty spawner
     * @return the spawner item, or {@code null} if the entity type is not spawnable
     */
    @Nullable
    ItemStack getSpawnerItem(@Nullable EntityType entityType);

    /**
     * Reads the entity type back out of a spawner item.
     *
     * @return the entity type, or {@code null} if the item is no spawner item, is not
     *         recognized as a SilkSpawners item or represents an empty spawner
     */
    @Nullable
    EntityType getEntityType(@Nullable ItemStack itemStack);

    /**
     * @return whether the item is a spawner item, {@code false} for {@code null}
     */
    boolean isSpawnerItem(@Nullable ItemStack itemStack);

    /**
     * Reads a placed spawner block.
     *
     * @return a snapshot of the spawner, or {@code null} if the block is no spawner
     */
    @Nullable
    SpawnerSnapshot getSpawner(Block block);

    /**
     * Changes the entity type of a placed spawner block. The change is applied one tick later.
     *
     * @param entityType the new entity type, {@code null} to empty the spawner
     * @return {@code false} if the block is no spawner or the entity type is not spawnable
     */
    boolean setSpawnerType(Block block, @Nullable EntityType entityType);

    /**
     * @return all entity types a spawner can be set to; an empty spawner
     *         ({@code null} entity type) is also supported
     */
    Set<EntityType> getSupportedEntityTypes();

    /**
     * Checks whether breaking a spawner of the given entity type would drop it for the player,
     * evaluating the break permission and the held tool against the silk touch configuration.
     * The configured drop chance is not part of this check.
     *
     * @param entityType the spawner's entity type, {@code null} for an empty spawner
     */
    boolean canSilkDrop(Player player, @Nullable EntityType entityType);
}
