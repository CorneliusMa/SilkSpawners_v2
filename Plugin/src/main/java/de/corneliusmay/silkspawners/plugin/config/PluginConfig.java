package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.config.formatters.BooleanConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.IntegerConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.MessageConfigValue;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public enum PluginConfig {
    MESSAGE_PREFIX(ConfigScope.MESSAGES, "prefix", "$8[$bSilkSpawners$8]", new MessageConfigValue()),
    MESSAGE_LOCALE(ConfigScope.MESSAGES, "locale", "en", (ConfigValueFormatter<Locale>) Locale::forLanguageTag, new String[]{ConfigScope.MESSAGES.getPath() + "lcoale"}),
    SPAWNER_DROP_CHANCE(ConfigScope.SPAWNER, "dropChance", 100, new IntegerConfigValue()),
    SPAWNER_DESTROYABLE(ConfigScope.SPAWNER, "destroyable", true, new BooleanConfigValue()),
    SPAWNER_PICKAXE_REQUIRED(ConfigScope.SPAWNER, "pickaxeRequired", true, new BooleanConfigValue()),
    SPAWNER_SILKTOUCH_REQUIRED(ConfigScope.SPAWNER, "silktouchRequired", true, new BooleanConfigValue()),
    SPAWNER_ITEM_NAME(ConfigScope.SPAWNER_ITEM, "name", "$dSpawner", new MessageConfigValue()),
    SPAWNER_ITEM_PREFIX(ConfigScope.SPAWNER_ITEM, "prefix", "$e", value -> value.isEmpty() ? "§f" : new MessageConfigValue().format(value)),
    SPAWNER_ITEM_PREFIX_OLD(ConfigScope.SPAWNER_ITEM, "prefixOld", "", new MessageConfigValue(), new String[]{ConfigScope.SPAWNER_ITEM.getPath() + "prefix-old"}),
    SPAWNER_ITEM_LORE(ConfigScope.SPAWNER_ITEM, "lore", new Object[]{new String[0], new String[0]}, new MessageConfigValue()),
    SPAWNER_EXPLOSION_NORMAL(ConfigScope.SPAWNER_EXPLOSION, "normal", 0, new IntegerConfigValue()),
    SPAWNER_EXPLOSION_SILKTOUCH(ConfigScope.SPAWNER_EXPLOSION, "silktouch", 0, new IntegerConfigValue()),
    SPAWNER_MESSAGE_DENY_DESTROY(ConfigScope.SPAWNER_MESSAGES, "denyDestroy", true, new BooleanConfigValue()),
    SPAWNER_MESSAGE_DENY_PLACE(ConfigScope.SPAWNER_MESSAGES, "denyPlace", true, new BooleanConfigValue()),
    SPAWNER_MESSAGE_DENY_CHANGE(ConfigScope.SPAWNER_MESSAGES, "denyChange", true, new BooleanConfigValue()),
    SPAWNER_PERMISSION_DISABLE_DESTROY(ConfigScope.SPAWNER_PERMISSIONS, "disableDestroy", false, new BooleanConfigValue()),
    SPAWNER_PERMISSION_DISABLE_PLACE(ConfigScope.SPAWNER_PERMISSIONS, "disablePlace", false, new BooleanConfigValue()),
    SPAWNER_PERMISSION_DISABLE_CHANGE(ConfigScope.SPAWNER_PERMISSIONS, "disableChange", false, new BooleanConfigValue()),
    UPDATE_CONFIG_VERSION(ConfigScope.UPDATE, "configVersion", 2, new IntegerConfigValue()),
    UPDATE_CHECK_ENABLED(ConfigScope.UPDATE_CHECK, "enabled", true, new BooleanConfigValue()),
    UPDATE_CHECK_INTERVAL(ConfigScope.UPDATE_CHECK, "interval", 24, new IntegerConfigValue()),
    ;

    public static final int CONFIG_VERSION = 2;

    @Getter
    private FileConfiguration config;

    @Getter
    private final String path;

    @Getter
    private final ConfigValueFormatter<?> formatter;

    private final Object[] defaultValue;

    private String[] legacyKeys;

    PluginConfig(ConfigScope scope, String key, Object defaultValue, ConfigValueFormatter<?> formatter) {
        this.path = scope.getPath() + key;
        this.defaultValue = new Object[CONFIG_VERSION];
        this.defaultValue[CONFIG_VERSION - 1] = defaultValue;
        this.formatter = formatter;
    }

    PluginConfig(ConfigScope scope, String key, Object[] defaultValue, ConfigValueFormatter<?> formatter) {
        this.path = scope.getPath() + key;
        this.defaultValue = defaultValue;
        this.formatter = formatter;
    }

    PluginConfig(ConfigScope scope, String key, Object defaultValue, ConfigValueFormatter<?> formatter, String[] legacyKeys) {
        this.path = scope.getPath() + key;
        this.defaultValue = new Object[CONFIG_VERSION];
        this.defaultValue[CONFIG_VERSION - 1] = defaultValue;
        this.formatter = formatter;
        this.legacyKeys = legacyKeys;
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
