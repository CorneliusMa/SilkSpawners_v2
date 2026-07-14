package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.plugin.loader.Loader;
import de.corneliusmay.silkspawners.plugin.platform.PlatformLoader;
import de.corneliusmay.silkspawners.plugin.version.CrossVersionHandler;

public class SpawnerLoader implements Loader {

    private final CrossVersionHandler versionHandler;

    private final PlatformLoader platformLoader;

    public SpawnerLoader(CrossVersionHandler versionHandler, PlatformLoader platformLoader) {
        this.versionHandler = versionHandler;
        this.platformLoader = platformLoader;
    }

    @Override
    public boolean load() {
        SpawnerContext.commit(versionHandler.getBukkitHandler(), platformLoader.getServerPlatform());
        return true;
    }
}
