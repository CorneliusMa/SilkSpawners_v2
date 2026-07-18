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

    private final Map<Class<? extends Loader>, Loader> loaded = new HashMap<>();

    public PluginLoader(SilkSpawners plugin) {
        this.plugin = plugin;
        ambient = new Object[] {plugin};
    }

    public boolean load() {
        for (Class<? extends Loader> type : WiredComponents.LOAD_ORDER) {
            Loader loader = create(type);
            if (!loader.load()) return backOff();
            loaded.put(type, loader);
        }
        return true;
    }

    public <T extends Loader> T get(Class<T> type) {
        return type.cast(loaded.get(type));
    }

    // Picks up every matching @Wired class, so new components never need manual registration
    public <T> List<T> createAll(Class<T> supertype, Object... arguments) {
        return WiredComponents.PARAMETERS.keySet().stream()
                .filter(supertype::isAssignableFrom)
                .sorted(Comparator.comparing(Class::getName))
                .map(type -> supertype.cast(create(type, arguments)))
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
        if (Loader.class.isAssignableFrom(parameter)) {
            Loader dependency = loaded.get(parameter);
            if (dependency != null) return dependency;
            throw new IllegalStateException("Loader dependency is not loaded: " + parameter.getName());
        }
        Class<? extends Loader> owner = WiredComponents.PRODUCT_OWNERS.get(parameter);
        if (owner != null) {
            Loader provider = loaded.get(owner);
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

    private boolean backOff() {
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        return false;
    }
}
