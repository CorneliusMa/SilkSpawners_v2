package de.corneliusmay.silkspawners.plugin.spawner.policy;

import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import org.bukkit.entity.Player;

public record SpawnerTypeProfile(
        String permissionRoot,
        String commandPermissionInfix,
        String localeKeyPrefix,
        ConfigKey<Integer> dropChance,
        ConfigKey<Boolean> destroyable,
        ConfigKey<Boolean> pickaxeRequired,
        ConfigKey<Boolean> silktouchRequired,
        ConfigKey<Integer> silktouchLevel,
        ConfigKey<Boolean> permissionDisableDestroy,
        ConfigKey<Boolean> permissionDisablePlace,
        ConfigKey<Boolean> permissionDisableChange,
        ConfigKey<Boolean> messageDenyDestroy,
        ConfigKey<Boolean> messageDenyPlace,
        ConfigKey<Boolean> messageDenyChange) {

    public boolean canBreak(Player player, String entity) {
        return allows(player, "break", entity, permissionDisableDestroy);
    }

    public boolean canPlace(Player player, String entity) {
        return allows(player, "place", entity, permissionDisablePlace);
    }

    public boolean canChange(Player player, String entity) {
        return allows(player, "change", entity, permissionDisableChange);
    }

    public String localeKey(String suffix) {
        return localeKeyPrefix + "_" + suffix;
    }

    private boolean allows(Player player, String action, String entity, ConfigKey<Boolean> disabled) {
        return player.hasPermission(permissionRoot + "." + action + "." + entity)
                || player.hasPermission(permissionRoot + "." + action + ".*")
                || disabled.get();
    }
}
