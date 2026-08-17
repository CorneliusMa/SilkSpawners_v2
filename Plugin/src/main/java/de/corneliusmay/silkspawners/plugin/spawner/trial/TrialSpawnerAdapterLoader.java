package de.corneliusmay.silkspawners.plugin.spawner.trial;

import static de.corneliusmay.silkspawners.plugin.version.MinecraftVersionChecker.getTrialSpawnerVersion;

import de.corneliusmay.silkspawners.plugin.capability.Capabilities;
import de.corneliusmay.silkspawners.plugin.capability.Capability;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.plugin.version.MinecraftVersion;
import de.corneliusmay.silkspawners.spi.version.TrialSpawnerAdapter;
import lombok.RequiredArgsConstructor;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Provides;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
@Singleton
class TrialSpawnerAdapterLoader implements Loader {

    private static final String NAME = "Trial spawners";

    private static final String COOLDOWN_API_CLASS = "org.bukkit.block.TrialSpawner";

    private static final String COOLDOWN_API_METHOD = "getCooldownEnd";

    private final ComponentLoader<TrialSpawnerAdapter> loader =
            new ComponentLoader<>(TrialSpawnerAdapter.class, "bukkit");

    private final Capabilities capabilities;

    private final MinecraftVersion version;

    private TrialSpawnerAdapter adapter = new UnsupportedTrialSpawners();

    @Provides
    public TrialSpawnerAdapter getAdapter() {
        return adapter;
    }

    @Override
    public boolean load() {
        adapter = loadCapability().orElse(new UnsupportedTrialSpawners());
        return true;
    }

    private Capability<TrialSpawnerAdapter> loadCapability() {
        String module = getTrialSpawnerVersion(version);
        if (module == null) return capabilities.unavailable(NAME);
        return capabilities.probe(
                NAME,
                COOLDOWN_API_CLASS,
                COOLDOWN_API_METHOD,
                () -> loader.instantiate(module + ".TrialSpawnerImplementation"));
    }
}
