package de.corneliusmay.silkspawners.plugin.config.handler;

public interface ConfigValueFormatter<T> {

    /**
     * Formats a value from its string form.
     */
    T format(String value);

    /**
     * Formats the raw value as read from the config file.
     * By default, the value is stringified and passed to {@link #format(String)}.
     *
     * @param value the raw config value, may be {@code null}
     * @return the formatted value
     */
    default T format(Object value) {
        return format(value == null ? null : value.toString());
    }
}
