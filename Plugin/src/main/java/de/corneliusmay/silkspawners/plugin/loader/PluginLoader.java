package de.corneliusmay.silkspawners.plugin.loader;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import java.util.HashMap;
import java.util.Map;

public class PluginLoader {

    private final SilkSpawners plugin;

    private final Map<Class<? extends Loader>, Loader> loaded = new HashMap<>();

    public PluginLoader(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    public boolean load(Loader... loaders) {
        for (Loader loader : loaders) {
            if (!loader.load()) return backOff();
            loaded.put(loader.getClass(), loader);
        }
        return true;
    }

    public <T extends Loader> T get(Class<T> type) {
        return type.cast(loaded.get(type));
    }

    private boolean backOff() {
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        return false;
    }
}
