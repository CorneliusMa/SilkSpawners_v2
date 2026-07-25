package de.corneliusmay.silkspawners.plugin.spawner;

import java.util.Locale;
import java.util.Map;
import org.bukkit.entity.EntityType;

final class EntityNames {

    // Each legacy name maps to its immediate successor, so resolution stops at the first name the server knows
    private static final Map<String, String> RENAMES = Map.ofEntries(
            Map.entry("cavespider", "cave_spider"),
            Map.entry("enderdragon", "ender_dragon"),
            Map.entry("entityhorse", "horse"),
            Map.entry("lavaslime", "magma_cube"),
            Map.entry("mushroomcow", "mooshroom"),
            Map.entry("ozelot", "ocelot"),
            Map.entry("pigzombie", "zombie_pigman"),
            Map.entry("polarbear", "polar_bear"),
            Map.entry("villagergolem", "villager_golem"),
            Map.entry("witherboss", "wither"),
            Map.entry("snowman", "snow_golem"),
            Map.entry("evocation_illager", "evoker"),
            Map.entry("illusion_illager", "illusioner"),
            Map.entry("vindication_illager", "vindicator"),
            Map.entry("villager_golem", "iron_golem"),
            Map.entry("zombie_pigman", "zombified_piglin"));

    private EntityNames() {}

    static EntityType resolve(String serializedName) {
        for (String name = serializedName; name != null; name = RENAMES.get(name)) {
            EntityType entityType = EntityType.fromName(name);
            if (entityType != null) return entityType;
        }
        try {
            return EntityType.valueOf(serializedName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
