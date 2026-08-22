package de.corneliusmay.silkspawners.plugin.config.formatters;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueException;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;

public class DecimalConfigValue implements ConfigValueFormatter<Double> {

    private final double min;

    private final double max;

    public DecimalConfigValue(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Double format(String value) {
        double parsed = number(value);
        if (parsed < min || parsed > max)
            throw new ConfigValueException("Expected a number between " + min + " and " + max);
        return parsed;
    }

    @Override
    public Object parse(String input) {
        return number(input);
    }

    private double number(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException ex) {
            throw new ConfigValueException("Expected a number", ex);
        }
    }
}
