package de.corneliusmay.silkspawners.plugin.capability;

import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoadException;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor
public class Capabilities implements Dumpable {

    private final Logger logger;

    private final List<Capability<?>> capabilities = new ArrayList<>();

    @Override
    public void describe(DumpObject<?> writer) {
        capabilities.forEach(capability -> capability.describe(writer));
    }

    public <T> Capability<T> load(String name, Supplier<T> loader) {
        try {
            return available(name, loader.get());
        } catch (ComponentLoadException ex) {
            return failed(name, ex);
        }
    }

    public <T> Capability<T> probe(String name, String requiredClass, Supplier<T> loader) {
        return probeIf(name, classExists(requiredClass), loader);
    }

    public <T> Capability<T> probe(String name, String requiredClass, String requiredMethod, Supplier<T> loader) {
        return probeIf(name, methodExists(requiredClass, requiredMethod), loader);
    }

    private <T> Capability<T> probeIf(String name, boolean present, Supplier<T> loader) {
        if (!present) return unavailable(name);
        try {
            return available(name, loader.get());
        } catch (RuntimeException | LinkageError ex) {
            return failed(name, ex);
        }
    }

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static boolean methodExists(String className, String methodName) {
        try {
            Class.forName(className).getMethod(methodName);
            return true;
        } catch (ReflectiveOperationException ex) {
            return false;
        }
    }

    public <T> Capability<T> unavailable(String name) {
        logger.info(name + ": not supported on this server");
        return register(Capability.unavailable(name));
    }

    private <T> Capability<T> available(String name, T implementation) {
        logger.info(name + ": supported on this server");
        return register(new Capability<>(name, implementation));
    }

    private <T> Capability<T> failed(String name, Throwable ex) {
        logger.warn(name + ": failed to load, continuing without support", ex);
        return register(Capability.failed(name));
    }

    private <T> Capability<T> register(Capability<T> capability) {
        capabilities.add(capability);
        return capability;
    }
}
