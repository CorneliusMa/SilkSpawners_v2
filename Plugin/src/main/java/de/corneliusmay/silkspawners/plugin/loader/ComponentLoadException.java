package de.corneliusmay.silkspawners.plugin.loader;

public class ComponentLoadException extends RuntimeException {

    ComponentLoadException(String className, Throwable cause) {
        super("Failed to load component " + className, cause);
    }
}
