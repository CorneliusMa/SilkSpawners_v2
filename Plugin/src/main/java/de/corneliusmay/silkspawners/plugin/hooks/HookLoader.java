package de.corneliusmay.silkspawners.plugin.hooks;

import de.corneliusmay.silkspawners.api.Hook;
import de.corneliusmay.silkspawners.api.SpawnerProvider;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
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

    private Class<?> loadClass(String hookName) throws ClassNotFoundException {
        return Class.forName("de.corneliusmay.silkspawners.hooks." + hookName);
    }

    private void register(HookDefinition definition) {
        if (registeredHooks.contains(definition.hookName())) return;
        if (Objects.isNull(pluginManager.getPlugin(definition.pluginName()))) return;

        if (!new ConfigValue<Boolean>(definition.enabledConfig()).get()) return;

        try {
            Class<?> clazz = loadClass(definition.hookName());
            Hook hook = (Hook) clazz.getConstructor(JavaPlugin.class, SpawnerProvider.class).newInstance(plugin, spawnerProvider);

            hook.register();
            registeredHooks.add(definition.hookName());
            plugin.getLog().info("Hooked into " + definition.pluginName());
        } catch (Exception e) {
            plugin.getLog().error("Failed to load hook " + definition.hookName(), e);
        }
    }
}
