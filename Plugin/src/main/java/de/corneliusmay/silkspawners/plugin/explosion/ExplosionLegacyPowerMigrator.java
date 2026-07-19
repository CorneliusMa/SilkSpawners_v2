package de.corneliusmay.silkspawners.plugin.explosion;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class ExplosionLegacyPowerMigrator implements ConfigValueMigrator {

    @Override
    public Object migrate(Object legacyValue, ConfigurationSection legacyConfig) {
        Integer power = legacyPower(legacyValue);
        if (power == null) return null;

        List<Map<String, Object>> tiers = new ArrayList<>();
        if (power < 1) return tiers;

        Map<String, Object> tier = new LinkedHashMap<>();
        tier.put("chance", 100);
        tier.put("power", power);
        tiers.add(tier);
        return tiers;
    }

    private Integer legacyPower(Object legacyValue) {
        if (legacyValue instanceof Number number) return number.intValue();
        if (!(legacyValue instanceof String string)) return null;
        try {
            return Integer.parseInt(string.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
