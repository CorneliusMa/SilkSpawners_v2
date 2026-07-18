package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.StringUtils;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Spawner implements SpawnerSnapshot {

    public static final String EMPTY = "empty";

    @Getter
    private final EntityType entityType;

    private final ItemStack itemStack;

    Spawner(EntityType entityType, ItemStack itemStack) {
        this.entityType = entityType;
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack == null ? null : itemStack.clone();
    }

    public String serializedEntityType() {
        return serializedEntityType(entityType);
    }

    @Override
    public String getDisplayName() {
        return displayName(entityType);
    }

    public String coloredName() {
        return PluginConfig.SPAWNER_ITEM_COLOR.get() + displayName(entityType);
    }

    public boolean isEmpty() {
        return entityType == null;
    }

    boolean isValid() {
        return itemStack != null && (isEmpty() || entityType.isSpawnable());
    }

    static String serializedEntityType(EntityType entityType) {
        return entityType == null ? EMPTY : entityType.getName().toLowerCase();
    }

    static String itemName(EntityType entityType) {
        return PluginConfig.SPAWNER_ITEM_NAME.get().replace("{entity}", displayName(entityType));
    }

    static String serializedName(EntityType entityType) {
        return PluginConfig.SPAWNER_ITEM_PREFIX.get() + displayName(entityType);
    }

    private static String displayName(EntityType entityType) {
        return StringUtils.capitalizeFully(serializedEntityType(entityType).replace("_", " "));
    }
}
