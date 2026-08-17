package de.corneliusmay.silkspawners.plugin.spawner.trial;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.spi.version.TrialSpawnerAdapter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

class UnsupportedTrialSpawners implements TrialSpawnerAdapter {

    @Override
    public Material getTrialSpawnerMaterial() {
        return null;
    }

    @Override
    public boolean isTrialSpawner(Block block) {
        return false;
    }

    @Override
    public TrialSpawnerState readState(Block block) {
        return null;
    }

    @Override
    public void applyState(Block block, TrialSpawnerState state) {}

    @Override
    public void withholdRewards(Block block, int ticks) {}

    @Override
    public EntityType spawnEggEntityType(ItemStack item) {
        return null;
    }

    @Override
    public void installLootGuard(Plugin plugin) {}
}
