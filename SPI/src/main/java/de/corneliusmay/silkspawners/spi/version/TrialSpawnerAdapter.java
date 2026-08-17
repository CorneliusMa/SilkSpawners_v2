package de.corneliusmay.silkspawners.spi.version;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface TrialSpawnerAdapter {

    // Null when the server version has no trial spawners
    Material getTrialSpawnerMaterial();

    boolean isTrialSpawner(Block block);

    TrialSpawnerState readState(Block block);

    void applyState(Block block, TrialSpawnerState state);

    void withholdRewards(Block block, int ticks);

    EntityType spawnEggEntityType(ItemStack item);

    void installLootGuard(Plugin plugin);
}
