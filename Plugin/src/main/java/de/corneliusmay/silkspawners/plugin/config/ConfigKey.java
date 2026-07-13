package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import java.util.List;
import java.util.NavigableMap;
import lombok.AccessLevel;
import lombok.Getter;

public final class ConfigKey<T> {

    @Getter
    private final String path;

    @Getter(AccessLevel.PACKAGE)
    private final ConfigValueFormatter<?> formatter;

    @Getter(AccessLevel.PACKAGE)
    private final Object defaultValue;

    @Getter(AccessLevel.PACKAGE)
    private final String[] legacyKeys;

    @Getter(AccessLevel.PACKAGE)
    private final boolean list;

    @Getter(AccessLevel.PACKAGE)
    private final NavigableMap<Integer, List<ConfigValueMigrator>> migrators;

    ConfigKey(ConfigKeyBuilder builder) {
        this.path = builder.scope.getPath() + builder.key;
        this.formatter = builder.formatter;
        this.defaultValue = builder.defaultValue;
        this.legacyKeys = builder.legacyKeys;
        this.list = builder.list;
        this.migrators = builder.migrators;
        ConfigRegistry.register(this);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) ConfigRegistry.value(this);
    }
}
