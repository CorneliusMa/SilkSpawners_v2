package de.corneliusmay.silkspawners.plugin.capability;

import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import java.util.Optional;

public class Capability<T> {

    private final String name;

    private final T implementation;

    private final boolean failed;

    Capability(String name, T implementation) {
        this(name, implementation, false);
    }

    private Capability(String name, T implementation, boolean failed) {
        this.name = name;
        this.implementation = implementation;
        this.failed = failed;
    }

    protected Capability(Capability<T> capability) {
        this(capability.name, capability.implementation, capability.failed);
    }

    public static <T> Capability<T> unavailable(String name) {
        return new Capability<>(name, null);
    }

    static <T> Capability<T> failed(String name) {
        return new Capability<>(name, null, true);
    }

    public boolean isAvailable() {
        return implementation != null;
    }

    public Optional<T> get() {
        return Optional.ofNullable(implementation);
    }

    public T orElse(T fallback) {
        return implementation == null ? fallback : implementation;
    }

    void describe(DumpObject<?> writer) {
        var section = writer.section(name.toLowerCase().replace(' ', '-')).value("supported", isAvailable());
        if (failed) section.value("failed", true);
        if (implementation != null)
            section.value("implementation", implementation.getClass().getName());
    }
}
