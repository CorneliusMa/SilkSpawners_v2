package de.corneliusmay.silkspawners.plugin.spawner.trial;

import de.corneliusmay.silkspawners.api.TrialSpawnerConfig;
import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.plugin.entity.EntityNames;
import de.corneliusmay.silkspawners.plugin.entity.StoredEntityNames;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.entity.EntityType;

// The stored format of every trial spawner item's state. Breaking it silently resets existing
// items to vanilla behavior. Unknown keys are ignored so later versions can add fields
class TrialSpawnerStateFormat {

    private static final int VERSION = 1;

    private static final int DEFAULT_COOLDOWN_LENGTH = 36000;

    private static final TrialSpawnerConfig DEFAULT_CONFIG = new TrialSpawnerConfig(null, 40, 14, 4, 6, 2, 2, 1, null);

    private static final String ENTRY_SEPARATOR = ",";
    private static final String VALUE_SEPARATOR = "=";
    private static final String REWARD_SEPARATOR = ";";
    private static final String WEIGHT_SEPARATOR = "\\|";

    private TrialSpawnerStateFormat() {}

    static TrialSpawnerState defaultState(EntityType entityType) {
        TrialSpawnerConfig config = new TrialSpawnerConfig(
                entityType,
                DEFAULT_CONFIG.delay(),
                DEFAULT_CONFIG.requiredPlayerRange(),
                DEFAULT_CONFIG.spawnRange(),
                DEFAULT_CONFIG.baseSpawnsBeforeCooldown(),
                DEFAULT_CONFIG.baseSimultaneousEntities(),
                DEFAULT_CONFIG.additionalSpawnsBeforeCooldown(),
                DEFAULT_CONFIG.additionalSimultaneousEntities(),
                DEFAULT_CONFIG.possibleRewards());
        return new TrialSpawnerState(false, DEFAULT_COOLDOWN_LENGTH, false, config, config);
    }

    static String serialize(TrialSpawnerState state) {
        StringBuilder serialized = new StringBuilder("v" + VALUE_SEPARATOR + VERSION);
        append(serialized, "ominous", state.ominous());
        append(serialized, "cooldownLength", state.cooldownLength());
        append(serialized, "cooldownPending", state.cooldownPending());
        serialize(serialized, "normal", state.normal());
        serialize(serialized, "ominousConfig", state.ominousConfig());
        return serialized.toString();
    }

    static TrialSpawnerState deserialize(String serialized) {
        if (serialized == null) return null;
        Map<String, String> values = new LinkedHashMap<>();
        for (String entry : serialized.split(ENTRY_SEPARATOR)) {
            String[] pair = entry.split(VALUE_SEPARATOR, 2);
            if (pair.length != 2) return null;
            values.put(pair[0], pair[1]);
        }
        return new TrialSpawnerState(
                bool(values, "ominous"),
                integer(values, "cooldownLength", DEFAULT_COOLDOWN_LENGTH),
                bool(values, "cooldownPending"),
                deserialize(values, "normal"),
                deserialize(values, "ominousConfig"));
    }

    private static void serialize(StringBuilder serialized, String prefix, TrialSpawnerConfig config) {
        append(serialized, prefix + ".entity", EntityNames.serialized(config.entityType()));
        append(serialized, prefix + ".delay", config.delay());
        append(serialized, prefix + ".requiredPlayerRange", config.requiredPlayerRange());
        append(serialized, prefix + ".spawnRange", config.spawnRange());
        append(serialized, prefix + ".baseSpawns", config.baseSpawnsBeforeCooldown());
        append(serialized, prefix + ".baseEntities", config.baseSimultaneousEntities());
        append(serialized, prefix + ".additionalSpawns", config.additionalSpawnsBeforeCooldown());
        append(serialized, prefix + ".additionalEntities", config.additionalSimultaneousEntities());
        if (config.possibleRewards() != null)
            append(serialized, prefix + ".rewards", rewards(config.possibleRewards()));
    }

    private static TrialSpawnerConfig deserialize(Map<String, String> values, String prefix) {
        return new TrialSpawnerConfig(
                entityType(values.get(prefix + ".entity")),
                integer(values, prefix + ".delay", DEFAULT_CONFIG.delay()),
                integer(values, prefix + ".requiredPlayerRange", DEFAULT_CONFIG.requiredPlayerRange()),
                integer(values, prefix + ".spawnRange", DEFAULT_CONFIG.spawnRange()),
                decimal(values, prefix + ".baseSpawns", DEFAULT_CONFIG.baseSpawnsBeforeCooldown()),
                decimal(values, prefix + ".baseEntities", DEFAULT_CONFIG.baseSimultaneousEntities()),
                decimal(values, prefix + ".additionalSpawns", DEFAULT_CONFIG.additionalSpawnsBeforeCooldown()),
                decimal(values, prefix + ".additionalEntities", DEFAULT_CONFIG.additionalSimultaneousEntities()),
                rewards(values.get(prefix + ".rewards")));
    }

    private static void append(StringBuilder serialized, String key, Object value) {
        serialized.append(ENTRY_SEPARATOR).append(key).append(VALUE_SEPARATOR).append(value);
    }

    private static String rewards(Map<String, Integer> rewards) {
        StringBuilder serialized = new StringBuilder();
        rewards.forEach((table, weight) -> {
            if (!serialized.isEmpty()) serialized.append(REWARD_SEPARATOR);
            serialized.append(table).append('|').append(weight);
        });
        return serialized.toString();
    }

    private static Map<String, Integer> rewards(String serialized) {
        if (serialized == null) return null;
        Map<String, Integer> rewards = new LinkedHashMap<>();
        if (serialized.isEmpty()) return rewards;
        for (String entry : serialized.split(REWARD_SEPARATOR)) {
            String[] pair = entry.split(WEIGHT_SEPARATOR, 2);
            if (pair.length != 2) continue;
            try {
                rewards.put(pair[0], Integer.parseInt(pair[1]));
            } catch (NumberFormatException ignored) {
                // A reward we cannot read is dropped rather than voiding the whole item
            }
        }
        return rewards;
    }

    private static EntityType entityType(String serialized) {
        if (serialized == null || serialized.equals(EntityNames.EMPTY)) return null;
        return StoredEntityNames.resolve(serialized);
    }

    private static boolean bool(Map<String, String> values, String key) {
        return Boolean.parseBoolean(values.get(key));
    }

    private static int integer(Map<String, String> values, String key, int fallback) {
        try {
            return values.containsKey(key) ? Integer.parseInt(values.get(key)) : fallback;
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static float decimal(Map<String, String> values, String key, float fallback) {
        try {
            return values.containsKey(key) ? Float.parseFloat(values.get(key)) : fallback;
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
