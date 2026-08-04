package de.corneliusmay.silkspawners.plugin.config;

import static de.corneliusmay.silkspawners.plugin.config.ConfigApply.*;
import static de.corneliusmay.silkspawners.plugin.config.ConfigScope.*;

import de.corneliusmay.silkspawners.plugin.config.formatters.BooleanConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.IntegerConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.MessageConfigValue;
import de.corneliusmay.silkspawners.plugin.config.migrators.InheritValueMigrator;
import de.corneliusmay.silkspawners.plugin.config.migrators.LegacyDefaultMigrator;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionLegacyPowerMigrator;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTier;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTierListConfigValue;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerLoreMigrator;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerPrefixOldMigrator;
import java.util.List;
import java.util.Locale;
import org.weftkit.wiring.StaticHolder;

@StaticHolder
public final class PluginConfig {

    public static final int CONFIG_VERSION = 4;

    public static final ConfigKey<String> MESSAGE_PREFIX = message(MESSAGES, "prefix", "$8[$bSilkSpawners$8]");
    public static final ConfigKey<Locale> MESSAGE_LOCALE = builder(MESSAGES, "locale")
            .def("en")
            .legacy(MESSAGES.getPath() + "lcoale")
            .apply(AFTER_RELOAD)
            .formatter(Locale::forLanguageTag);
    public static final ConfigKey<Integer> SPAWNER_DROP_CHANCE = integer(SPAWNER, "dropChance", 100, 0, 100);
    public static final ConfigKey<Boolean> SPAWNER_DESTROYABLE = bool(SPAWNER, "destroyable", true);
    public static final ConfigKey<Boolean> SPAWNER_PICKAXE_REQUIRED = bool(SPAWNER, "pickaxeRequired", true);
    public static final ConfigKey<Boolean> SPAWNER_SILKTOUCH_REQUIRED = bool(SPAWNER, "silktouchRequired", true);
    public static final ConfigKey<Integer> SPAWNER_SILKTOUCH_LEVEL =
            integer(SPAWNER, "silktouchLevel", 1, 1, Integer.MAX_VALUE);
    public static final ConfigKey<String> SPAWNER_ITEM_NAME = builder(SPAWNER_ITEM, "name")
            .def("$d{entity} Spawner")
            .migrator(4, new LegacyDefaultMigrator("$dSpawner", "$d{entity} Spawner"))
            .formatter(new MessageConfigValue());
    public static final ConfigKey<String> SPAWNER_ITEM_COLOR = builder(SPAWNER_ITEM, "color")
            .def("$e")
            .migrator(4, new InheritValueMigrator(SPAWNER_ITEM.getPath() + "prefix"))
            .formatter(new MessageConfigValue());
    public static final ConfigKey<List<String>> SPAWNER_ITEM_PREFIX_OLD = builder(SPAWNER_ITEM, "prefixOld")
            .def(new String[0])
            .internal()
            .legacy(SPAWNER_ITEM.getPath() + "prefix-old")
            .migrator(4, new SpawnerPrefixOldMigrator(SPAWNER_ITEM.getPath() + "prefix", "$e", "$f"))
            .listFormatter(new MessageConfigValue());
    public static final ConfigKey<List<String>> SPAWNER_ITEM_LORE = builder(SPAWNER_ITEM, "lore")
            .def(new String[] {"$7Spawns $e{entity}"})
            .legacy(SPAWNER_ITEM.getPath() + "prefix")
            .migrator(4, new SpawnerLoreMigrator(SPAWNER_ITEM.getPath() + "prefix", "$e", "$7Spawns $e{entity}"))
            .listFormatter(new MessageConfigValue());
    public static final ConfigKey<List<ExplosionTier>> SPAWNER_EXPLOSION_ALL =
            builder(SPAWNER_EXPLOSION, "all").def(List.of()).formatter(new ExplosionTierListConfigValue());
    public static final ConfigKey<List<ExplosionTier>> SPAWNER_EXPLOSION_NORMAL = builder(SPAWNER_EXPLOSION, "normal")
            .def(List.of())
            .migrator(3, new ExplosionLegacyPowerMigrator())
            .formatter(new ExplosionTierListConfigValue());
    public static final ConfigKey<List<ExplosionTier>> SPAWNER_EXPLOSION_SILKTOUCH = builder(
                    SPAWNER_EXPLOSION, "silktouch")
            .def(List.of())
            .migrator(3, new ExplosionLegacyPowerMigrator())
            .formatter(new ExplosionTierListConfigValue());
    public static final ConfigKey<Boolean> SPAWNER_MESSAGE_DENY_DESTROY = bool(SPAWNER_MESSAGES, "denyDestroy", true);
    public static final ConfigKey<Boolean> SPAWNER_MESSAGE_DENY_PLACE = bool(SPAWNER_MESSAGES, "denyPlace", true);
    public static final ConfigKey<Boolean> SPAWNER_MESSAGE_DENY_CHANGE = bool(SPAWNER_MESSAGES, "denyChange", true);
    public static final ConfigKey<Boolean> SPAWNER_PERMISSION_DISABLE_DESTROY =
            bool(SPAWNER_PERMISSIONS, "disableDestroy", false);
    public static final ConfigKey<Boolean> SPAWNER_PERMISSION_DISABLE_PLACE =
            bool(SPAWNER_PERMISSIONS, "disablePlace", false);
    public static final ConfigKey<Boolean> SPAWNER_PERMISSION_DISABLE_CHANGE =
            bool(SPAWNER_PERMISSIONS, "disableChange", false);
    public static final ConfigKey<Integer> UPDATE_CONFIG_VERSION =
            builder(UPDATE, "configVersion").def(CONFIG_VERSION).internal().formatter(new IntegerConfigValue());
    public static final ConfigKey<Boolean> UPDATE_CHECK_ENABLED =
            builder(UPDATE_CHECK, "enabled").def(true).apply(AFTER_RELOAD).formatter(new BooleanConfigValue());
    public static final ConfigKey<Integer> UPDATE_CHECK_INTERVAL = builder(UPDATE_CHECK, "interval")
            .def(24)
            .apply(AFTER_RELOAD)
            .formatter(new IntegerConfigValue(1, Integer.MAX_VALUE));
    public static final ConfigKey<Boolean> HOOK_SHOPGUIPLUS =
            builder(HOOKS, "shopguiplus").def(true).apply(AFTER_RESTART).formatter(new BooleanConfigValue());

    private PluginConfig() {}

    private static ConfigKeyBuilder builder(ConfigScope scope, String key) {
        return new ConfigKeyBuilder(scope, key);
    }

    private static ConfigKey<Boolean> bool(ConfigScope scope, String key, boolean def) {
        return builder(scope, key).def(def).formatter(new BooleanConfigValue());
    }

    private static ConfigKey<Integer> integer(ConfigScope scope, String key, int def, int min, int max) {
        return builder(scope, key).def(def).formatter(new IntegerConfigValue(min, max));
    }

    private static ConfigKey<String> message(ConfigScope scope, String key, String def) {
        return builder(scope, key).def(def).formatter(new MessageConfigValue());
    }

    public static List<ConfigKey<?>> values() {
        return ConfigRegistry.keys();
    }

    public static ConfigKey<?> byPath(String path) {
        return values().stream()
                .filter(key -> key.getPath().equalsIgnoreCase(path))
                .findFirst()
                .orElse(null);
    }
}
