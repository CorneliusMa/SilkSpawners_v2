package de.corneliusmay.silkspawners.plugin.explosion;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueMigrator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExplosionLegacyPowerMigrator implements ConfigValueMigrator {

    @Override
    public Object migrate(Object legacyValue) {
        if (!(legacyValue instanceof Number power)) return null;

        List<Map<String, Object>> tiers = new ArrayList<>();
        if (power.intValue() < 1) return tiers;

        Map<String, Object> tier = new LinkedHashMap<>();
        tier.put("chance", 100);
        tier.put("power", power.intValue());
        tiers.add(tier);
        return tiers;
    }
}
