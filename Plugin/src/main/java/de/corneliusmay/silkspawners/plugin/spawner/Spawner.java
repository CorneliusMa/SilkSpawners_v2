package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.StringUtils;
import de.corneliusmay.silkspawners.spi.spawner.SpawnerSettings;
import java.util.List;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Spawner implements SpawnerSnapshot {

    public static final String EMPTY = "empty";

    @Getter
    private final EntityType entityType;

    private final ItemStack itemStack;

    @Getter
    private final SpawnerSettings settings;

    Spawner(EntityType entityType, ItemStack itemStack, SpawnerSettings settings) {
        this.entityType = entityType;
        this.itemStack = itemStack;
        this.settings = settings;
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
        if (entityType == null) return EMPTY;
        String name = entityType.getName();
        return (name == null ? entityType.name() : name).toLowerCase();
    }

    static String itemName(EntityType entityType) {
        return PluginConfig.SPAWNER_ITEM_NAME.get().replace("{entity}", displayName(entityType));
    }

    static List<String> itemLore(EntityType entityType) {
        String entityName = entityType == null ? "Nothing" : displayName(entityType);
        return PluginConfig.SPAWNER_ITEM_LORE.get().stream()
                .map(line -> line.replace("{entity}", entityName))
                .toList();
    }

    public static String displayName(EntityType entityType) {
        return StringUtils.capitalizeFully(serializedEntityType(entityType).replace("_", " "));
    }
}
