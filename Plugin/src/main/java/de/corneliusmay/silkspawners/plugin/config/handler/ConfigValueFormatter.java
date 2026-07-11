package de.corneliusmay.silkspawners.plugin.config.handler;

public interface ConfigValueFormatter<T> {

    T format(String value);

    default T format(Object value) {
        return format(value == null ? null : value.toString());
    }
}
