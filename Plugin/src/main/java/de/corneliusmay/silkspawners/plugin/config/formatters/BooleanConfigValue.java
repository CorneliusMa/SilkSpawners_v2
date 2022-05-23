package de.corneliusmay.silkspawners.plugin.config.formatters;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;

public class BooleanConfigValue implements ConfigValueFormatter<Boolean> {
    @Override
    public Boolean format(String value) {
        return Boolean.parseBoolean(value);
    }
}
