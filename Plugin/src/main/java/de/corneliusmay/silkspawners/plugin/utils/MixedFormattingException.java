package de.corneliusmay.silkspawners.plugin.utils;

public class MixedFormattingException extends RuntimeException {

    public MixedFormattingException() {
        super("Formatting codes cannot be mixed with MiniMessage tags");
    }
}
