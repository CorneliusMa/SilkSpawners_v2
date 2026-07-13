package de.corneliusmay.silkspawners.plugin.loader;

import java.lang.reflect.Constructor;

public class ComponentLoader<T> {

    private static final String BASE_PACKAGE = "de.corneliusmay.silkspawners";

    private final Class<T> type;

    private final String packagePrefix;

    private final Class<?>[] constructorSignature;

    public ComponentLoader(Class<T> type, String subPackage, Class<?>... constructorSignature) {
        this.type = type;
        this.packagePrefix = BASE_PACKAGE + "." + subPackage + ".";
        this.constructorSignature = constructorSignature;
    }

    public T instantiate(String relativeName, Object... args) {
        String className = packagePrefix + relativeName;
        try {
            Constructor<? extends T> constructor =
                    Class.forName(className).asSubclass(type).getConstructor(constructorSignature);
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException | ClassCastException | IllegalArgumentException e) {
            throw new ComponentLoadException(className, e);
        }
    }
}
