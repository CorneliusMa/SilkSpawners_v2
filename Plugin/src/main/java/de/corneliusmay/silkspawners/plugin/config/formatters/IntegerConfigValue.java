package de.corneliusmay.silkspawners.plugin.config.formatters;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueException;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;

public class IntegerConfigValue implements ConfigValueFormatter<Integer> {

    private final int min;

    private final int max;

    public IntegerConfigValue() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerConfigValue(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer format(String value) {
        int parsed = number(value);
        if (parsed < min || parsed > max) throw new ConfigValueException(rangeMessage());
        return parsed;
    }

    @Override
    public Object parse(String input) {
        return number(input);
    }

    private int number(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new ConfigValueException("Expected a whole number", ex);
        }
    }

    private String rangeMessage() {
        if (max == Integer.MAX_VALUE) return "Expected a whole number of at least " + min;
        return "Expected a whole number between " + min + " and " + max;
    }
}
