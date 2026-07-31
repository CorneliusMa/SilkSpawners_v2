package de.corneliusmay.silkspawners.plugin.config.formatters;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueException;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import java.util.List;

public class BooleanConfigValue implements ConfigValueFormatter<Boolean> {

    private static final List<String> VALUES = List.of("true", "false");

    @Override
    public Boolean format(String value) {
        return Boolean.parseBoolean(value);
    }

    @Override
    public Object parse(String input) {
        if (VALUES.stream().noneMatch(input::equalsIgnoreCase))
            throw new ConfigValueException("Expected true or false");
        return Boolean.parseBoolean(input);
    }

    @Override
    public List<String> suggestions() {
        return VALUES;
    }
}
