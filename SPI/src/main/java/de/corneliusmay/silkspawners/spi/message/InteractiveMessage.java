package de.corneliusmay.silkspawners.spi.message;

public record InteractiveMessage(String prefix, String link, String suffix, ClickAction action) {}
