package de.corneliusmay.silkspawners.bukkit.v1_21_4;

import de.corneliusmay.silkspawners.api.TrialSpawnerConfig;
import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.spi.version.TrialSpawnerAdapter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TrialSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.loot.LootTable;
import org.bukkit.spawner.TrialSpawnerConfiguration;

public class TrialSpawnerImplementation implements TrialSpawnerAdapter {

    @Override
    public Material getTrialSpawnerMaterial() {
        return Material.TRIAL_SPAWNER;
    }

    @Override
    public TrialSpawnerState readState(Block block) {
        if (!(block.getState() instanceof TrialSpawner spawner)) return null;
        return new TrialSpawnerState(
                spawner.isOminous(),
                spawner.getCooldownLength(),
                cooldownRemaining(block, spawner),
                read(spawner.getNormalConfiguration()),
                read(spawner.getOminousConfiguration()));
    }

    private int cooldownRemaining(Block block, TrialSpawner spawner) {
        return (int) Math.max(0, spawner.getCooldownEnd() - block.getWorld().getGameTime());
    }

    @Override
    public void applyState(Block block, TrialSpawnerState state) {
        BlockState blockState = block.getState();
        if (!(blockState instanceof TrialSpawner spawner)) return;

        apply(spawner.getNormalConfiguration(), state.normal());
        apply(spawner.getOminousConfiguration(), state.ominousConfig());
        spawner.setCooldownLength(state.cooldownLength());
        spawner.setOminous(state.ominous());
        int cooldownRemaining = Math.min(state.cooldownRemaining(), state.cooldownLength());
        if (cooldownRemaining > 0) spawner.setCooldownEnd(block.getWorld().getGameTime() + cooldownRemaining);
        syncBlockData(blockState, state.ominous(), cooldownRemaining > 0);

        blockState.update(true, false);
    }

    // The ominous flag and the machine state live in the block entity and in the block data, and
    // the update above rewrites the block data captured before the setters ran
    private void syncBlockData(BlockState blockState, boolean ominous, boolean coolingDown) {
        BlockData blockData = blockState.getBlockData();
        if (!(blockData instanceof org.bukkit.block.data.type.TrialSpawner trialSpawner)) return;
        trialSpawner.setOminous(ominous);
        if (coolingDown) trialSpawner.setTrialSpawnerState(org.bukkit.block.data.type.TrialSpawner.State.COOLDOWN);
        blockState.setBlockData(trialSpawner);
    }

    private TrialSpawnerConfig read(TrialSpawnerConfiguration configuration) {
        Map<String, Integer> rewards = new LinkedHashMap<>();
        configuration
                .getPossibleRewards()
                .forEach((table, weight) -> rewards.put(table.getKey().toString(), weight));
        return new TrialSpawnerConfig(
                configuration.getSpawnedType(),
                configuration.getDelay(),
                configuration.getRequiredPlayerRange(),
                configuration.getSpawnRange(),
                configuration.getBaseSpawnsBeforeCooldown(),
                configuration.getBaseSimultaneousEntities(),
                configuration.getAdditionalSpawnsBeforeCooldown(),
                configuration.getAdditionalSimultaneousEntities(),
                rewards);
    }

    private void apply(TrialSpawnerConfiguration configuration, TrialSpawnerConfig config) {
        configuration.setSpawnedType(config.entityType());
        configuration.setDelay(config.delay());
        configuration.setRequiredPlayerRange(config.requiredPlayerRange());
        configuration.setSpawnRange(config.spawnRange());
        configuration.setBaseSpawnsBeforeCooldown(config.baseSpawnsBeforeCooldown());
        configuration.setBaseSimultaneousEntities(config.baseSimultaneousEntities());
        configuration.setAdditionalSpawnsBeforeCooldown(config.additionalSpawnsBeforeCooldown());
        configuration.setAdditionalSimultaneousEntities(config.additionalSimultaneousEntities());
        applyRewards(configuration, config.possibleRewards());
    }

    private void applyRewards(TrialSpawnerConfiguration configuration, Map<String, Integer> rewards) {
        if (rewards == null) return;

        Map<LootTable, Integer> tables = lootTables(rewards);
        if (tables.size() < rewards.size())
            Bukkit.getLogger()
                    .warning("SilkSpawners: dropped " + (rewards.size() - tables.size())
                            + " trial spawner reward table(s) this server does not know");

        // Writing an empty map would strip the block's own tables, which is worse than keeping a
        // reward set this server can no longer reproduce
        if (tables.isEmpty() && !rewards.isEmpty()) return;
        configuration.setPossibleRewards(tables);
    }

    private Map<LootTable, Integer> lootTables(Map<String, Integer> rewards) {
        Map<LootTable, Integer> tables = new LinkedHashMap<>();
        rewards.forEach((key, weight) -> {
            NamespacedKey namespacedKey = NamespacedKey.fromString(key);
            LootTable table = namespacedKey == null ? null : Bukkit.getLootTable(namespacedKey);
            if (table != null && weight != null && weight >= 1) tables.put(table, weight);
        });
        return tables;
    }
}
