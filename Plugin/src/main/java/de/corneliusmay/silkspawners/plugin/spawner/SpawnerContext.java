package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.spi.version.Bukkit;
import lombok.AccessLevel;
import lombok.Getter;

final class SpawnerContext {

    private static volatile SpawnerContext context;

    @Getter(AccessLevel.PACKAGE)
    private final Bukkit bukkitHandler;

    @Getter(AccessLevel.PACKAGE)
    private final ServerPlatform platform;

    private SpawnerContext(Bukkit bukkitHandler, ServerPlatform platform) {
        this.bukkitHandler = bukkitHandler;
        this.platform = platform;
    }

    static void commit(Bukkit bukkitHandler, ServerPlatform platform) {
        context = new SpawnerContext(bukkitHandler, platform);
    }

    static SpawnerContext get() {
        SpawnerContext current = context;
        if (current == null) throw new IllegalStateException("Spawner dependencies are not loaded");
        return current;
    }
}
