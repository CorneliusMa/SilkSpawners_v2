package de.corneliusmay.silkspawners.plugin.spawner.trial;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.spi.version.TrialSpawnerAdapter;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
}
