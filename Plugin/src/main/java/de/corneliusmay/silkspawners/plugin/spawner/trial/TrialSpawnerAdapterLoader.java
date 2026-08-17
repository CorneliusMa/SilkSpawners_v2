package de.corneliusmay.silkspawners.plugin.spawner.trial;

import de.corneliusmay.silkspawners.plugin.capability.Capabilities;
import de.corneliusmay.silkspawners.plugin.capability.Capability;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.plugin.version.MinecraftVersion;
import de.corneliusmay.silkspawners.plugin.version.VersionModules;
import de.corneliusmay.silkspawners.spi.version.TrialSpawnerAdapter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Provides;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
@Singleton
class TrialSpawnerAdapterLoader implements Loader {

    private static final String NAME = "Trial spawners";

    private final ComponentLoader<TrialSpawnerAdapter> loader =
            new ComponentLoader<>(TrialSpawnerAdapter.class, "bukkit");

    private final Capabilities capabilities;

    private final MinecraftVersion version;

    private final Plugin plugin;

    private TrialSpawnerAdapter adapter = new UnsupportedTrialSpawners();

    @Provides
    public TrialSpawnerAdapter getAdapter() {
        return adapter;
    }

    @Override
    public boolean load() {
        adapter = loadCapability().orElse(new UnsupportedTrialSpawners());
        adapter.installLootGuard(plugin);
        return true;
    }

    private Capability<TrialSpawnerAdapter> loadCapability() {
        String module = VersionModules.trialSpawner(version);
        if (module == null) return capabilities.unavailable(NAME);
        return capabilities.load(NAME, () -> loader.instantiate(module + ".TrialSpawnerImplementation"));
    }
}
