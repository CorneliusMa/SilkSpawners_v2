package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.config.formatters.BooleanConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.IntegerConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.MessageConfigValue;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionLegacyPowerMigrator;
import de.corneliusmay.silkspawners.plugin.explosion.ExplosionTierListConfigValue;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;

import static de.corneliusmay.silkspawners.plugin.config.ConfigScope.*;

public enum PluginConfig {
    MESSAGE_PREFIX(builder(MESSAGES, "prefix").def("$8[$bSilkSpawners$8]").formatter(new MessageConfigValue())),
    MESSAGE_LOCALE(builder(MESSAGES, "locale").def("en").formatter((ConfigValueFormatter<Locale>) Locale::forLanguageTag).legacy(MESSAGES.getPath() + "lcoale")),
    SPAWNER_DROP_CHANCE(builder(SPAWNER, "dropChance").def(100).formatter(new IntegerConfigValue())),
    SPAWNER_DESTROYABLE(builder(SPAWNER, "destroyable").def(true).formatter(new BooleanConfigValue())),
    SPAWNER_PICKAXE_REQUIRED(builder(SPAWNER, "pickaxeRequired").def(true).formatter(new BooleanConfigValue())),
    SPAWNER_SILKTOUCH_REQUIRED(builder(SPAWNER, "silktouchRequired").def(true).formatter(new BooleanConfigValue())),
    SPAWNER_SILKTOUCH_LEVEL(builder(SPAWNER, "silktouchLevel").def(1).formatter(new IntegerConfigValue())),
    SPAWNER_ITEM_NAME(builder(SPAWNER_ITEM, "name").def("$dSpawner").formatter(new MessageConfigValue())),
    SPAWNER_ITEM_PREFIX(builder(SPAWNER_ITEM, "prefix").def("$e").formatter(value -> value.isEmpty() ? "§f" : new MessageConfigValue().format(value))),
    SPAWNER_ITEM_PREFIX_OLD(builder(SPAWNER_ITEM, "prefixOld").def("").formatter(new MessageConfigValue()).legacy(SPAWNER_ITEM.getPath() + "prefix-old")),
    SPAWNER_ITEM_LORE(builder(SPAWNER_ITEM, "lore").def(new String[0]).formatter(new MessageConfigValue()).list()),
    SPAWNER_EXPLOSION_NORMAL(builder(SPAWNER_EXPLOSION, "normal").def(new ArrayList<>()).formatter(new ExplosionTierListConfigValue()).migrator(3, new ExplosionLegacyPowerMigrator())),
    SPAWNER_EXPLOSION_SILKTOUCH(builder(SPAWNER_EXPLOSION, "silktouch").def(new ArrayList<>()).formatter(new ExplosionTierListConfigValue()).migrator(3, new ExplosionLegacyPowerMigrator())),
    SPAWNER_MESSAGE_DENY_DESTROY(builder(SPAWNER_MESSAGES, "denyDestroy").def(true).formatter(new BooleanConfigValue())),
    SPAWNER_MESSAGE_DENY_PLACE(builder(SPAWNER_MESSAGES, "denyPlace").def(true).formatter(new BooleanConfigValue())),
    SPAWNER_MESSAGE_DENY_CHANGE(builder(SPAWNER_MESSAGES, "denyChange").def(true).formatter(new BooleanConfigValue())),
    SPAWNER_PERMISSION_DISABLE_DESTROY(builder(SPAWNER_PERMISSIONS, "disableDestroy").def(false).formatter(new BooleanConfigValue())),
    SPAWNER_PERMISSION_DISABLE_PLACE(builder(SPAWNER_PERMISSIONS, "disablePlace").def(false).formatter(new BooleanConfigValue())),
    SPAWNER_PERMISSION_DISABLE_CHANGE(builder(SPAWNER_PERMISSIONS, "disableChange").def(false).formatter(new BooleanConfigValue())),
    UPDATE_CONFIG_VERSION(builder(UPDATE, "configVersion").def(PluginConfig.CONFIG_VERSION).formatter(new IntegerConfigValue())),
    UPDATE_CHECK_ENABLED(builder(UPDATE_CHECK, "enabled").def(true).formatter(new BooleanConfigValue())),
    UPDATE_CHECK_INTERVAL(builder(UPDATE_CHECK, "interval").def(24).formatter(new IntegerConfigValue())),
    HOOK_SHOPGUIPLUS(builder(HOOKS, "shopguiplus").def(true).formatter(new BooleanConfigValue())),
    ;

    public static final int CONFIG_VERSION = 3;

    @Getter
    private FileConfiguration config;

    @Getter
    private final String path;

    @Getter
    private final ConfigValueFormatter<?> formatter;

    private final Object defaultValue;

    private final String[] legacyKeys;

    @Getter
    private final boolean list;

    private final NavigableMap<Integer, List<ConfigValueMigrator>> migrators;

    PluginConfig(PluginConfigBuilder builder) {
        this.path = builder.scope.getPath() + builder.key;
        this.formatter = builder.formatter;
        this.defaultValue = builder.defaultValue;
        this.legacyKeys = builder.legacyKeys;
        this.list = builder.list;
        this.migrators = builder.migrators;
    }

    private static PluginConfigBuilder builder(ConfigScope scope, String key) {
        return new PluginConfigBuilder(scope, key);
    }

    public void init(FileConfiguration config, Integer initialVersion) {
        this.config = config;

        if (initialVersion < CONFIG_VERSION && !migrators.isEmpty()) {
            Object value = config.get(path, null);
            boolean migrated = false;
            for (List<ConfigValueMigrator> versionMigrators : migrators.tailMap(initialVersion, false).values()) {
                for (ConfigValueMigrator migrator : versionMigrators) {
                    Object result = migrator.migrate(value);
                    if (result != null) {
                        value = result;
                        migrated = true;
                    }
                }
            }
            if (migrated) config.set(path, value);
        }

        if (legacyKeys != null && legacyKeys.length > 0 && initialVersion != CONFIG_VERSION) {
            int legacyKeyIndex = legacyKeys.length >= initialVersion ? initialVersion : legacyKeys.length;
            String legacyKey = legacyKeys[legacyKeyIndex - 1];
            Object legacyValue = config.get(legacyKey);
            if (legacyValue != null) {
                config.addDefault(path, legacyValue);
                config.set(legacyKey, null);
                return;
            }
        }

        config.addDefault(path, defaultValue);
    }
}
