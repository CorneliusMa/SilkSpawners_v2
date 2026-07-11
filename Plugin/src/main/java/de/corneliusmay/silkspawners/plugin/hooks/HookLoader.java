package de.corneliusmay.silkspawners.plugin.hooks;

import de.corneliusmay.silkspawners.api.Hook;
import de.corneliusmay.silkspawners.api.SpawnerProvider;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class HookLoader {

    private record HookDefinition(String hookName, String pluginName, PluginConfig enabledConfig) {}

    private final SilkSpawners plugin;

    private final PluginManager pluginManager;

    private final SpawnerProvider spawnerProvider;

    private final List<HookDefinition> hooks = new ArrayList<>();

    private final Set<String> registeredHooks = new HashSet<>();

    private final ComponentLoader<Hook> loader = new ComponentLoader<>(Hook.class, "hooks", JavaPlugin.class, SpawnerProvider.class);

    public HookLoader(SilkSpawners plugin) {
        this.plugin = plugin;
        this.pluginManager = Bukkit.getPluginManager();
        this.spawnerProvider = new SilkSpawnersProvider(plugin);
    }

    public void addHook(String hookName, String pluginName, PluginConfig enabledConfig) {
        hooks.add(new HookDefinition(hookName, pluginName, enabledConfig));
    }

    public void register() {
        hooks.forEach(this::register);
    }

    private void register(HookDefinition definition) {
        if (registeredHooks.contains(definition.hookName())) return;
        if (Objects.isNull(pluginManager.getPlugin(definition.pluginName()))) return;

        if (!new ConfigValue<Boolean>(definition.enabledConfig()).get()) return;

        try {
            Hook hook = loader.instantiate(definition.hookName(), plugin, spawnerProvider);

            hook.register();
            registeredHooks.add(definition.hookName());
            plugin.getLog().info("Hooked into " + definition.pluginName());
        } catch (Exception e) {
            plugin.getLog().error("Failed to load hook " + definition.hookName(), e);
        }
    }
}
