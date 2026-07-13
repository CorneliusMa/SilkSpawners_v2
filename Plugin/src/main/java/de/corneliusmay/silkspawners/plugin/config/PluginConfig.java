package de.corneliusmay.silkspawners.plugin.config;

import static de.corneliusmay.silkspawners.plugin.config.ConfigScope.*;

import de.corneliusmay.silkspawners.plugin.config.formatters.BooleanConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.IntegerConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.MessageConfigValue;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionLegacyPowerMigrator;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTier;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTierListConfigValue;
import java.util.List;
import java.util.Locale;

public final class PluginConfig {

    public static final int CONFIG_VERSION = 3;

    public static final ConfigKey<String> MESSAGE_PREFIX = message(MESSAGES, "prefix", "$8[$bSilkSpawners$8]");
    public static final ConfigKey<Locale> MESSAGE_LOCALE = builder(MESSAGES, "locale")
            .def("en")
            .legacy(MESSAGES.getPath() + "lcoale")
            .formatter(Locale::forLanguageTag);
    public static final ConfigKey<Integer> SPAWNER_DROP_CHANCE = integer(SPAWNER, "dropChance", 100);
    public static final ConfigKey<Boolean> SPAWNER_DESTROYABLE = bool(SPAWNER, "destroyable", true);
    public static final ConfigKey<Boolean> SPAWNER_PICKAXE_REQUIRED = bool(SPAWNER, "pickaxeRequired", true);
    public static final ConfigKey<Boolean> SPAWNER_SILKTOUCH_REQUIRED = bool(SPAWNER, "silktouchRequired", true);
    public static final ConfigKey<Integer> SPAWNER_SILKTOUCH_LEVEL = integer(SPAWNER, "silktouchLevel", 1);
    public static final ConfigKey<String> SPAWNER_ITEM_NAME = message(SPAWNER_ITEM, "name", "$dSpawner");
    public static final ConfigKey<String> SPAWNER_ITEM_PREFIX = builder(SPAWNER_ITEM, "prefix")
            .def("$e")
            .formatter(value -> value.isEmpty() ? "§f" : new MessageConfigValue().format(value));
    public static final ConfigKey<String> SPAWNER_ITEM_PREFIX_OLD = builder(SPAWNER_ITEM, "prefixOld")
            .def("")
            .legacy(SPAWNER_ITEM.getPath() + "prefix-old")
            .formatter(new MessageConfigValue());
    public static final ConfigKey<List<String>> SPAWNER_ITEM_LORE =
            builder(SPAWNER_ITEM, "lore").def(new String[0]).listFormatter(new MessageConfigValue());
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
    public static final ConfigKey<Integer> UPDATE_CONFIG_VERSION = integer(UPDATE, "configVersion", CONFIG_VERSION);
    public static final ConfigKey<Boolean> UPDATE_CHECK_ENABLED = bool(UPDATE_CHECK, "enabled", true);
    public static final ConfigKey<Integer> UPDATE_CHECK_INTERVAL = integer(UPDATE_CHECK, "interval", 24);
    public static final ConfigKey<Boolean> HOOK_SHOPGUIPLUS = bool(HOOKS, "shopguiplus", true);

    private PluginConfig() {}

    private static ConfigKeyBuilder builder(ConfigScope scope, String key) {
        return new ConfigKeyBuilder(scope, key);
    }

    private static ConfigKey<Boolean> bool(ConfigScope scope, String key, boolean def) {
        return builder(scope, key).def(def).formatter(new BooleanConfigValue());
    }

    private static ConfigKey<Integer> integer(ConfigScope scope, String key, int def) {
        return builder(scope, key).def(def).formatter(new IntegerConfigValue());
    }

    private static ConfigKey<String> message(ConfigScope scope, String key, String def) {
        return builder(scope, key).def(def).formatter(new MessageConfigValue());
    }

    static List<ConfigKey<?>> values() {
        return ConfigRegistry.keys();
    }
}
