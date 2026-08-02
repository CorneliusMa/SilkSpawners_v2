package de.corneliusmay.silkspawners.plugin.dump;

@FunctionalInterface
public interface Dumpable {

    void describe(DumpObject<?> writer);
}
