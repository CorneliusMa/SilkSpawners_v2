package de.corneliusmay.silkspawners.spi.version;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import java.util.function.BiPredicate;
import java.util.function.DoubleSupplier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface TrialSpawnerAdapter {

    Material getTrialSpawnerMaterial();

    TrialSpawnerState readState(Block block);

    void applyState(Block block, TrialSpawnerState state);

    void installBreakSpeedBoost(Plugin plugin, BiPredicate<Player, Block> eligible, DoubleSupplier multiplier);
}
