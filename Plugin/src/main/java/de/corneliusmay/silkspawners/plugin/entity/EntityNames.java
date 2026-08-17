package de.corneliusmay.silkspawners.plugin.entity;

import de.corneliusmay.silkspawners.plugin.utils.StringUtils;
import org.bukkit.entity.EntityType;

public final class EntityNames {

    public static final String EMPTY = "empty";

    private EntityNames() {}

    public static String serialized(EntityType entityType) {
        if (entityType == null) return EMPTY;
        String name = entityType.getName();
        return (name == null ? entityType.name() : name).toLowerCase();
    }

    public static String displayName(EntityType entityType) {
        return StringUtils.capitalizeFully(serialized(entityType).replace("_", " "));
    }
}
