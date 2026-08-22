package de.corneliusmay.silkspawners.plugin.spawner.trial;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.spi.version.TrialSpawnerAdapter;
import java.util.function.BiPredicate;
import java.util.function.DoubleSupplier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class UnsupportedTrialSpawners implements TrialSpawnerAdapter {

    @Override
    public Material getTrialSpawnerMaterial() {
        return null;
    }

    @Override
    public TrialSpawnerState readState(Block block) {
        return null;
    }

    @Override
    public void applyState(Block block, TrialSpawnerState state) {}

    @Override
    public void installBreakSpeedBoost(Plugin plugin, BiPredicate<Player, Block> eligible, DoubleSupplier multiplier) {}
}
