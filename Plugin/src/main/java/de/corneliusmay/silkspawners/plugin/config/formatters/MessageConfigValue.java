package de.corneliusmay.silkspawners.plugin.config.formatters;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;

public class MessageConfigValue implements ConfigValueFormatter<String> {
    @Override
    public String format(String value) {
        return value.replace("$", "§");
    }
}
