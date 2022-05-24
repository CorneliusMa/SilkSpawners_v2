package de.corneliusmay.silkspawners.plugin.config.handler;

public interface ConfigValueFormatter<T> {

    T format(String value);
}
