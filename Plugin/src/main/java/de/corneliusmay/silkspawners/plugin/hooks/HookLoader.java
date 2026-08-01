package de.corneliusmay.silkspawners.plugin.hooks;

import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import de.corneliusmay.silkspawners.plugin.dump.DumpEntry;
import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.hooks.Hook;
import de.corneliusmay.silkspawners.spi.hooks.SpawnerProvider;
import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Wired
public class HookLoader implements Loader, Dumpable {

    private record HookDefinition(String hookName, String pluginName, ConfigKey<Boolean> enabledConfig) {}

    private final JavaPlugin plugin;

    private final PluginManager pluginManager;

    private final SpawnerProvider spawnerProvider;

    private final List<HookDefinition> hooks = new ArrayList<>();

    private final Set<String> registeredHooks = new HashSet<>();

    private final ComponentLoader<Hook> loader =
            new ComponentLoader<>(Hook.class, "hooks", JavaPlugin.class, SpawnerProvider.class);

    public HookLoader(JavaPlugin plugin, SpawnerFactory spawnerFactory) {
        this.plugin = plugin;
        this.pluginManager = Bukkit.getPluginManager();
        this.spawnerProvider = new SilkSpawnersProvider(spawnerFactory);
    }

    @Override
    public boolean load() {
        return true;
    }

    public void addHook(String hookName, String pluginName, ConfigKey<Boolean> enabledConfig) {
        hooks.add(new HookDefinition(hookName, pluginName, enabledConfig));
    }

    public void register() {
        hooks.forEach(this::register);
    }

    @Override
    public void describe(DumpObject<?> writer) {
        DumpEntry<?> section = writer.section("hooks");
        hooks.forEach(hook -> section.section(hook.pluginName())
                .value("registered", registeredHooks.contains(hook.hookName()))
                .value("enabled", hook.enabledConfig().get())
                .value("plugin-present", pluginManager.getPlugin(hook.pluginName()) != null));
    }

    private void register(HookDefinition definition) {
        if (registeredHooks.contains(definition.hookName())) return;
        if (Objects.isNull(pluginManager.getPlugin(definition.pluginName()))) return;

        if (!definition.enabledConfig().get()) return;

        try {
            Hook hook = loader.instantiate(definition.hookName(), plugin, spawnerProvider);

            hook.register();
            registeredHooks.add(definition.hookName());
            Logger.info("Hooked into " + definition.pluginName());
        } catch (Exception e) {
            Logger.error("Failed to load hook " + definition.hookName(), e);
        }
    }
}
