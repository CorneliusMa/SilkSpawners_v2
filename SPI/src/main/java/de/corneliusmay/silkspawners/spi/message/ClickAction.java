package de.corneliusmay.silkspawners.spi.message;

public record ClickAction(ClickType type, String value) {

    public static ClickAction runCommand(String command) {
        return new ClickAction(ClickType.RUN_COMMAND, command);
    }

    public static ClickAction openUrl(String url) {
        return new ClickAction(ClickType.OPEN_URL, url);
    }
}
