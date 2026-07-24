package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.spi.spawner.SpawnerSettings;
import java.util.HashMap;
import java.util.Map;

// The stored format of every spawner item's settings. Breaking it silently resets existing items to vanilla behavior
class SpawnerSettingsFormat {

    private static final SpawnerSettings DEFAULT = new SpawnerSettings(200, 800, 4, 6, 16, 4);

    static SpawnerSettings nonDefault(SpawnerSettings settings) {
        return settings == null || DEFAULT.equals(settings) ? null : settings;
    }

    static String serialize(SpawnerSettings settings) {
        return "minSpawnDelay=" + settings.minSpawnDelay()
                + ",maxSpawnDelay=" + settings.maxSpawnDelay()
                + ",spawnCount=" + settings.spawnCount()
                + ",maxNearbyEntities=" + settings.maxNearbyEntities()
                + ",requiredPlayerRange=" + settings.requiredPlayerRange()
                + ",spawnRange=" + settings.spawnRange();
    }

    static SpawnerSettings deserialize(String serialized) {
        if (serialized == null) return null;
        Map<String, Integer> values = new HashMap<>();
        for (String entry : serialized.split(",")) {
            String[] pair = entry.split("=", 2);
            if (pair.length != 2) return null;
            try {
                values.put(pair[0], Integer.parseInt(pair[1]));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        SpawnerSettings settings = new SpawnerSettings(
                values.getOrDefault("minSpawnDelay", DEFAULT.minSpawnDelay()),
                values.getOrDefault("maxSpawnDelay", DEFAULT.maxSpawnDelay()),
                values.getOrDefault("spawnCount", DEFAULT.spawnCount()),
                values.getOrDefault("maxNearbyEntities", DEFAULT.maxNearbyEntities()),
                values.getOrDefault("requiredPlayerRange", DEFAULT.requiredPlayerRange()),
                values.getOrDefault("spawnRange", DEFAULT.spawnRange()));
        return isValid(settings) ? settings : null;
    }

    private static boolean isValid(SpawnerSettings settings) {
        return settings.minSpawnDelay() >= 0
                && settings.maxSpawnDelay() > 0
                && settings.minSpawnDelay() <= settings.maxSpawnDelay()
                && settings.spawnCount() >= 0
                && settings.maxNearbyEntities() >= 0
                && settings.requiredPlayerRange() >= 0
                && settings.spawnRange() >= 0;
    }
}
