package de.corneliusmay.silkspawners.plugin.config.handler;

public class ConfigValueException extends RuntimeException {

    public ConfigValueException(String message) {
        super(message);
    }

    public ConfigValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
