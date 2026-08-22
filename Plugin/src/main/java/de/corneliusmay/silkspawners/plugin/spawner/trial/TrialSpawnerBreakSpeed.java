package de.corneliusmay.silkspawners.plugin.spawner.trial;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SilkDropCheck;
import de.corneliusmay.silkspawners.spi.version.TrialSpawnerAdapter;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Qualified;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor
class TrialSpawnerBreakSpeed implements Loader {

    private final PluginConfig config;

    private final TrialSpawnerFactory trialSpawnerFactory;

    @Qualified("trialSpawner")
    private final SilkDropCheck silkDropCheck;

    private final TrialSpawnerAdapter trialSpawnerAdapter;

    private final Plugin plugin;

    @Override
    public boolean load() {
        trialSpawnerAdapter.installBreakSpeedBoost(plugin, this::boostEligible, this::multiplier);
        return true;
    }

    private double multiplier() {
        return config.TRIAL_SPAWNER_ENABLED.get() ? config.TRIAL_SPAWNER_BREAK_SPEED_MULTIPLIER.get() : 1;
    }

    private boolean boostEligible(Player player, Block block) {
        if (player.getGameMode() == GameMode.CREATIVE) return false;
        return trialSpawnerFactory
                .fromBlock(block)
                .map(trialSpawner -> silkDropCheck.canSilkDrop(player, trialSpawner.serializedEntityType()))
                .orElse(false);
    }
}
