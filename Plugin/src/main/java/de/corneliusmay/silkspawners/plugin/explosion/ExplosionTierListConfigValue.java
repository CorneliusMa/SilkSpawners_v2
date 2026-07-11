package de.corneliusmay.silkspawners.plugin.explosion;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExplosionTierListConfigValue implements ConfigValueFormatter<List<ExplosionTier>> {

    private static final List<String> KEYS = List.of("chance", "power", "setFire", "breakBlocks");

    @Override
    public List<ExplosionTier> format(String value) {
        throw new IllegalArgumentException("Expected a list of explosion tiers");
    }

    @Override
    public List<ExplosionTier> format(Object value) {
        if(value == null) return List.of();
        if(!(value instanceof List<?> tiers)) return format(String.valueOf(value));
        return tierList(tiers);
    }

    private List<ExplosionTier> tierList(List<?> entries) {
        List<ExplosionTier> tiers = new ArrayList<>();
        for(int i = 0; i < entries.size(); i++) {
            tiers.add(tier(entries.get(i), i));
        }
        tiers.sort(Comparator.comparingDouble(ExplosionTier::power).reversed());
        return List.copyOf(tiers);
    }

    private ExplosionTier tier(Object entry, int index) {
        if(!(entry instanceof Map<?, ?> values)) throw invalid(index, "must be a mapping with the keys " + KEYS);
        for(Object key : values.keySet()) {
            if(!KEYS.contains(String.valueOf(key))) throw invalid(index, "contains the unknown key '" + key + "' (allowed: " + KEYS + ")");
        }

        double power = number(values, "power", 0, index);
        if(power <= 0) throw invalid(index, "must define a 'power' greater than 0");
        double chance = number(values, "chance", 100, index);
        if(chance < 0) throw invalid(index, "must not have a negative 'chance'");

        return new ExplosionTier(chance, (float) power, bool(values, "setFire", false, index), bool(values, "breakBlocks", true, index));
    }

    private double number(Map<?, ?> values, String key, double defaultValue, int index) {
        Object value = values.get(key);
        if(value == null) return defaultValue;
        if(value instanceof Number number) return number.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw invalid(index, "has a non-numeric '" + key + "' value: " + value);
        }
    }

    private boolean bool(Map<?, ?> values, String key, boolean defaultValue, int index) {
        Object value = values.get(key);
        if(value == null) return defaultValue;
        if(value instanceof Boolean bool) return bool;
        String text = String.valueOf(value);
        if("true".equalsIgnoreCase(text) || "false".equalsIgnoreCase(text)) return Boolean.parseBoolean(text);
        throw invalid(index, "has a non-boolean '" + key + "' value: " + value);
    }

    private IllegalArgumentException invalid(int index, String message) {
        return new IllegalArgumentException("Explosion tier " + (index + 1) + " " + message);
    }
}
