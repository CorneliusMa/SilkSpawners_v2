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
    MESSAGE_LOCALE(ConfigScope.MESSAGES, "lcoale", "en", (ConfigValueFormatter<Locale>) Locale::forLanguageTag),
    SPAWNER_DROP_CHANCE(ConfigScope.SPAWNER, "dropChance", 100, new IntegerConfigValue()),
    SPAWNER_DESTROYABLE(ConfigScope.SPAWNER, "destroyable", true, new BooleanConfigValue()),
    SPAWNER_ITEM_NAME(ConfigScope.SPAWNER_ITEM, "name", "$dSpawner", new MessageConfigValue()),
    SPAWNER_ITEM_PREFIX(ConfigScope.SPAWNER_ITEM, "prefix", "$e", (ConfigValueFormatter<String>) value -> {
        if(value.equals("")) return "§f";
        return new MessageConfigValue().format(value);
    }),
    SPAWNER_ITEM_PREFIX_OLD(ConfigScope.SPAWNER_ITEM, "prefix-old", "", new MessageConfigValue()),
    SPAWNER_ITEM_LORE(ConfigScope.SPAWNER_ITEM, "lore", new String[0], new MessageConfigValue()),
    SPAWNER_EXPLOSION_NORMAL(ConfigScope.SPAWNER_EXPLOSION, "normal", 0, new IntegerConfigValue()),
    SPAWNER_EXPLOSION_SILKTOUCH(ConfigScope.SPAWNER_EXPLOSION, "silktouch", 0, new IntegerConfigValue()),
    SPAWNER_MESSAGE_DENY_DESTROY(ConfigScope.SPAWNER_MESSAGES, "denyDestroy", true, new BooleanConfigValue()),
    SPAWNER_MESSAGE_DENY_PLACE(ConfigScope.SPAWNER_MESSAGES, "denyPlace", true, new BooleanConfigValue()),
    SPAWNER_MESSAGE_DENY_CHANGE(ConfigScope.SPAWNER_MESSAGES, "denyChange", true, new BooleanConfigValue()),
    UPDATE_CHECK_ENABLED(ConfigScope.UPDATE_CHECK, "enabled", true, new BooleanConfigValue()),
    UPDATE_CHECK_INTERVAL(ConfigScope.UPDATE_CHECK, "interval", 24, new IntegerConfigValue()),
    ;

    @Getter
    private FileConfiguration config;

    @Getter
    private final String path;

    @Getter
    private final ConfigValueFormatter<?> formatter;

    private final Object defaultValue;

    PluginConfig(ConfigScope scope, String key, Object defaultValue, ConfigValueFormatter<?> formatter) {
        this.path = scope.getPath() + key;
        this.defaultValue = defaultValue;
        this.formatter = formatter;
    }

    public void init(FileConfiguration config) {
        this.config = config;
        config.addDefault(path, defaultValue);
    }
}
