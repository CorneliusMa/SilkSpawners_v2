package de.corneliusmay.silkspawners.wiring;

/**
 * Startup work a {@link Singleton} runs right after it is created. Returning false aborts the load.
 */
@FunctionalInterface
public interface Loader {

    boolean load();
}
