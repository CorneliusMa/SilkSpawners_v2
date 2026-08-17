package de.corneliusmay.silkspawners.plugin.spawner.policy;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import org.weftkit.wiring.Provides;
import org.weftkit.wiring.Qualified;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
class SpawnerPolicies {

    private final SpawnerTypeProfile spawnerProfile;

    private final SilkDropCheck spawnerCheck;

    SpawnerPolicies(PluginConfig config, VersionAdapter versionAdapter) {
        this.spawnerProfile = new SpawnerTypeProfile(
                "silkspawners",
                "",
                "SPAWNER",
                config.SPAWNER_DROP_CHANCE,
                config.SPAWNER_DESTROYABLE,
                config.SPAWNER_PICKAXE_REQUIRED,
                config.SPAWNER_SILKTOUCH_REQUIRED,
                config.SPAWNER_SILKTOUCH_LEVEL,
                config.SPAWNER_PERMISSION_DISABLE_DESTROY,
                config.SPAWNER_PERMISSION_DISABLE_PLACE,
                config.SPAWNER_PERMISSION_DISABLE_CHANGE,
                config.SPAWNER_MESSAGE_DENY_DESTROY,
                config.SPAWNER_MESSAGE_DENY_PLACE,
                config.SPAWNER_MESSAGE_DENY_CHANGE);
        this.spawnerCheck = new SilkDropCheck(versionAdapter, spawnerProfile);
    }

    @Provides
    @Qualified("spawner")
    SpawnerTypeProfile spawnerProfile() {
        return spawnerProfile;
    }

    @Provides
    @Qualified("spawner")
    SilkDropCheck spawnerCheck() {
        return spawnerCheck;
    }
}
