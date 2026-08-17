package de.corneliusmay.silkspawners.spi.version;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface TrialSpawnerAdapter {

    Material getTrialSpawnerMaterial();

    TrialSpawnerState readState(Block block);

    void applyState(Block block, TrialSpawnerState state);
}
