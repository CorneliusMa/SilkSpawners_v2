package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;
import lombok.AccessLevel;
import lombok.Getter;

public final class ConfigKey<T> {

    @Getter
    private final String path;

    @Getter(AccessLevel.PACKAGE)
    private final ConfigValueFormatter<?> formatter;

    private final Object defaultValue;

    @Getter(AccessLevel.PACKAGE)
    private final String[] legacyKeys;

    @Getter(AccessLevel.PACKAGE)
    private final boolean list;

    @Getter
    private final boolean internal;

    @Getter
    private final ConfigApply apply;

    @Getter(AccessLevel.PACKAGE)
    private final NavigableMap<Integer, List<ConfigValueMigrator>> migrators;

    ConfigKey(ConfigKeyBuilder builder) {
        this.path = builder.scope.getPath() + builder.key;
        this.formatter = builder.formatter;
        this.defaultValue = builder.defaultValue;
        this.legacyKeys = builder.legacyKeys;
        this.list = builder.list;
        this.internal = builder.internal;
        this.apply = builder.apply;
        this.migrators = builder.migrators;
        ConfigRegistry.register(this);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) ConfigRegistry.value(this);
    }

    public boolean isSettable() {
        return !internal && !list && formatter.supportsInput();
    }

    String getDescriptionKey() {
        return "CONFIG_" + path.toUpperCase(Locale.ROOT).replace('.', '_');
    }

    List<String> getSuggestions() {
        return formatter.suggestions();
    }

    Object parse(String input) {
        Object value = formatter.parse(input);
        formatter.format(value);
        return value;
    }

    void publish(Object value) {
        ConfigRegistry.update(this, formatter.format(value));
    }

    // Lists must reach the config as List, not array, or getStringList ignores the registered default
    Object getDefaultValue() {
        if (list) return Arrays.asList((String[]) defaultValue);
        return defaultValue;
    }

    Object formatDefault() {
        if (list)
            return Arrays.stream((String[]) defaultValue).map(formatter::format).toList();
        return formatter.format(defaultValue);
    }
}
