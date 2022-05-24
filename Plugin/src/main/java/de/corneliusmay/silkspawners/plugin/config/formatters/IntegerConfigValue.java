package de.corneliusmay.silkspawners.plugin.config.formatters;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;

public class IntegerConfigValue implements ConfigValueFormatter<Integer> {
    @Override
    public Integer format(String value) {
        return Integer.parseInt(value);
    }
}
