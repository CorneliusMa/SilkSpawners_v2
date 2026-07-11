package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.config.formatters.BooleanConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.IntegerConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.MessageConfigValue;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

import static de.corneliusmay.silkspawners.plugin.config.ConfigScope.*;

public enum PluginConfig {
    MESSAGE_PREFIX(builder(MESSAGES, "prefix").defs("$8[$bSilkSpawners$8]").formatter(new MessageConfigValue())),
    MESSAGE_LOCALE(builder(MESSAGES, "locale").defs("en").formatter((ConfigValueFormatter<Locale>) Locale::forLanguageTag).legacy(MESSAGES.getPath() + "lcoale")),
    SPAWNER_DROP_CHANCE(builder(SPAWNER, "dropChance").defs(100).formatter(new IntegerConfigValue())),
    SPAWNER_DESTROYABLE(builder(SPAWNER, "destroyable").defs(true).formatter(new BooleanConfigValue())),
    SPAWNER_PICKAXE_REQUIRED(builder(SPAWNER, "pickaxeRequired").defs(true).formatter(new BooleanConfigValue())),
    SPAWNER_SILKTOUCH_REQUIRED(builder(SPAWNER, "silktouchRequired").defs(true).formatter(new BooleanConfigValue())),
    SPAWNER_SILKTOUCH_LEVEL(builder(SPAWNER, "silktouchLevel").defs(1).formatter(new IntegerConfigValue())),
    SPAWNER_ITEM_NAME(builder(SPAWNER_ITEM, "name").defs("$dSpawner").formatter(new MessageConfigValue())),
    SPAWNER_ITEM_PREFIX(builder(SPAWNER_ITEM, "prefix").defs("$e").formatter(value -> value.isEmpty() ? "§f" : new MessageConfigValue().format(value))),
    SPAWNER_ITEM_PREFIX_OLD(builder(SPAWNER_ITEM, "prefixOld").defs("").formatter(new MessageConfigValue()).legacy(SPAWNER_ITEM.getPath() + "prefix-old")),
    SPAWNER_ITEM_LORE(builder(SPAWNER_ITEM, "lore").defs((Object) new String[0]).formatter(new MessageConfigValue()).list()),
    SPAWNER_EXPLOSION_NORMAL(builder(SPAWNER_EXPLOSION, "normal").defs(0).formatter(new IntegerConfigValue())),
    SPAWNER_EXPLOSION_SILKTOUCH(builder(SPAWNER_EXPLOSION, "silktouch").defs(0).formatter(new IntegerConfigValue())),
    SPAWNER_MESSAGE_DENY_DESTROY(builder(SPAWNER_MESSAGES, "denyDestroy").defs(true).formatter(new BooleanConfigValue())),
    SPAWNER_MESSAGE_DENY_PLACE(builder(SPAWNER_MESSAGES, "denyPlace").defs(true).formatter(new BooleanConfigValue())),
    SPAWNER_MESSAGE_DENY_CHANGE(builder(SPAWNER_MESSAGES, "denyChange").defs(true).formatter(new BooleanConfigValue())),
    SPAWNER_PERMISSION_DISABLE_DESTROY(builder(SPAWNER_PERMISSIONS, "disableDestroy").defs(false).formatter(new BooleanConfigValue())),
    SPAWNER_PERMISSION_DISABLE_PLACE(builder(SPAWNER_PERMISSIONS, "disablePlace").defs(false).formatter(new BooleanConfigValue())),
    SPAWNER_PERMISSION_DISABLE_CHANGE(builder(SPAWNER_PERMISSIONS, "disableChange").defs(false).formatter(new BooleanConfigValue())),
    UPDATE_CONFIG_VERSION(builder(UPDATE, "configVersion").defs(PluginConfig.CONFIG_VERSION).formatter(new IntegerConfigValue())),
    UPDATE_CHECK_ENABLED(builder(UPDATE_CHECK, "enabled").defs(true).formatter(new BooleanConfigValue())),
    UPDATE_CHECK_INTERVAL(builder(UPDATE_CHECK, "interval").defs(24).formatter(new IntegerConfigValue())),
    HOOK_SHOPGUIPLUS(builder(HOOKS, "shopguiplus").defs(true).formatter(new BooleanConfigValue())),
    ;

    public static final int CONFIG_VERSION = 2;

    @Getter
    private FileConfiguration config;

    @Getter
    private final String path;

    @Getter
    private final ConfigValueFormatter<?> formatter;

    private final Object[] defaultValue;

    private final String[] legacyKeys;

    @Getter
    private final boolean list;

    PluginConfig(PluginConfigBuilder builder) {
        this.path = builder.scope.getPath() + builder.key;
        this.formatter = builder.formatter;
        this.defaultValue = builder.defaultValue;
        this.legacyKeys = builder.legacyKeys;
        this.list = builder.list;
    }

    private static PluginConfigBuilder builder(ConfigScope scope, String key) {
        return new PluginConfigBuilder(scope, key);
    }

    public void init(FileConfiguration config, Integer initialVersion) {
        this.config = config;

        if (legacyKeys != null && legacyKeys.length > 0 && initialVersion != CONFIG_VERSION) {
            int legacyKeyIndex = legacyKeys.length >= initialVersion ? initialVersion : legacyKeys.length;
            String legacyKey = legacyKeys[legacyKeyIndex - 1];
            config.addDefault(path, config.get(legacyKey));
            config.set(legacyKey, null);
        } else {
            int defaultValueIndex = defaultValue.length >= initialVersion ? initialVersion : defaultValue.length;
            Object defaultValue = this.defaultValue[defaultValueIndex - 1];
            config.addDefault(path, defaultValue);
        }
    }
}
