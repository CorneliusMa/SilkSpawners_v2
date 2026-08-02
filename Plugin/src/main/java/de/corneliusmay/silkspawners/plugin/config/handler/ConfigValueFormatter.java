package de.corneliusmay.silkspawners.plugin.config.handler;

import java.util.List;

public interface ConfigValueFormatter<T> {

    T format(String value);

    default T format(Object value) {
        return format(value == null ? null : value.toString());
    }

    default boolean supportsInput() {
        return true;
    }

    default Object parse(String input) {
        return input;
    }

    default List<String> suggestions() {
        return List.of();
    }
}
