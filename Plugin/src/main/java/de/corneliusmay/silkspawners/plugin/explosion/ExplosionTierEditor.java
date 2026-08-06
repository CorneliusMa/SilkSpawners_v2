package de.corneliusmay.silkspawners.plugin.explosion;

import de.corneliusmay.silkspawners.plugin.config.ConfigEditor;
import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.weftkit.wiring.Requires;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@Requires(PluginConfig.class)
public class ExplosionTierEditor {

    private final ConfigEditor editor;

    private final Map<String, ConfigKey<List<ExplosionTier>>> scopes = new LinkedHashMap<>();

    public ExplosionTierEditor(ConfigEditor editor) {
        this.editor = editor;
        scopes.put("all", PluginConfig.SPAWNER_EXPLOSION_ALL);
        scopes.put("normal", PluginConfig.SPAWNER_EXPLOSION_NORMAL);
        scopes.put("silktouch", PluginConfig.SPAWNER_EXPLOSION_SILKTOUCH);
    }

    public List<String> scopeNames() {
        return List.copyOf(scopes.keySet());
    }

    public ConfigKey<List<ExplosionTier>> scope(String name) {
        return scopes.get(name.toLowerCase(Locale.ROOT));
    }

    public void add(ConfigKey<List<ExplosionTier>> scope, ExplosionTier tier) throws IOException {
        List<ExplosionTier> tiers = new ArrayList<>(scope.get());
        tiers.add(tier);
        tiers.sort(ExplosionTier.STRONGEST_FIRST);
        save(scope, tiers);
    }

    public void remove(ConfigKey<List<ExplosionTier>> scope, int index) throws IOException {
        List<ExplosionTier> tiers = new ArrayList<>(scope.get());
        tiers.remove(index);
        save(scope, tiers);
    }

    private void save(ConfigKey<List<ExplosionTier>> scope, List<ExplosionTier> tiers) throws IOException {
        editor.set(scope, tiers.stream().map(this::serialize).toList());
    }

    private Map<String, Object> serialize(ExplosionTier tier) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("chance", tier.chanceValue());
        values.put("power", tier.powerValue());
        values.put("setFire", tier.setFire());
        values.put("breakBlocks", tier.breakBlocks());
        return values;
    }
}
