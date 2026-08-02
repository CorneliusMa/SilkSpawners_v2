package de.corneliusmay.silkspawners.plugin.loader;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Registry;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Components come from the generated registry, so wiring contracts are already validated at compile time
@Registry
public class PluginLoader {

    private final SilkSpawners plugin;

    private final Object[] ambient;

    private final Map<Class<?>, Object> singletons = new HashMap<>();

    public PluginLoader(SilkSpawners plugin) {
        this.plugin = plugin;
        ambient = new Object[] {plugin};
    }

    public boolean load() {
        for (Class<?> type : WiredComponents.LOAD_ORDER) {
            Object singleton = create(type);
            if (singleton instanceof Loader loader && !loader.load()) return backOff();
            singletons.put(type, singleton);
        }
        return true;
    }

    public <T> T get(Class<T> type) {
        return type.cast(singletons.get(type));
    }

    // Picks up every matching @Wired class, so new components never need manual registration
    public <T> List<T> createAll(Class<T> supertype, Object... arguments) {
        return WiredComponents.PARAMETERS.keySet().stream()
                .filter(supertype::isAssignableFrom)
                .sorted(Comparator.comparing(Class::getName))
                .map(type -> supertype.cast(isSingleton(type) ? loaded(type) : create(type, arguments)))
                .toList();
    }

    public <T> T create(Class<T> type, Object... arguments) {
        Object[] parameters = parameters(type).stream()
                .map(parameter -> resolve(parameter, arguments, type))
                .toArray();
        try {
            return type.cast(WiredComponents.FACTORIES.get(type).apply(parameters));
        } catch (RuntimeException ex) {
            throw new ComponentLoadException(type.getName(), ex);
        }
    }

    private List<Class<?>> parameters(Class<?> type) {
        List<Class<?>> parameters = WiredComponents.PARAMETERS.get(type);
        if (parameters == null)
            throw new IllegalStateException("Component is not annotated with @Wired: " + type.getName());
        return parameters;
    }

    private Object resolve(Class<?> parameter, Object[] arguments, Class<?> component) {
        Object match = null;
        for (Object argument : arguments) {
            if (!parameter.isInstance(argument)) continue;
            if (match != null)
                throw new IllegalStateException(
                        "Ambiguous argument for " + parameter.getName() + " of " + component.getName());
            match = argument;
        }
        if (match != null) return match;
        for (Object provided : ambient) if (parameter.isInstance(provided)) return provided;
        if (isSingleton(parameter)) return loaded(parameter);
        Class<?> owner = WiredComponents.PRODUCT_OWNERS.get(parameter);
        if (owner != null) {
            Object provider = singletons.get(owner);
            Object product = provider == null
                    ? null
                    : WiredComponents.PRODUCT_GETTERS.get(parameter).apply(provider);
            if (product == null)
                throw new IllegalStateException("Dependency is not available yet: " + parameter.getName());
            return product;
        }
        if (WiredComponents.PARAMETERS.containsKey(parameter)) return create(parameter);
        throw new IllegalStateException(
                "Cannot resolve dependency " + parameter.getName() + " for " + component.getName());
    }

    private boolean isSingleton(Class<?> type) {
        return WiredComponents.LOAD_ORDER.contains(type);
    }

    private Object loaded(Class<?> type) {
        Object singleton = singletons.get(type);
        if (singleton == null) throw new IllegalStateException("Singleton dependency is not loaded: " + type.getName());
        return singleton;
    }

    private boolean backOff() {
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        return false;
    }
}
