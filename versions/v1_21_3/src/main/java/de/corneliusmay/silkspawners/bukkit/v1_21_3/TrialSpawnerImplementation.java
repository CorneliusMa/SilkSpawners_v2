package de.corneliusmay.silkspawners.bukkit.v1_21_3;

import de.corneliusmay.silkspawners.api.TrialSpawnerConfig;
import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.spi.version.TrialSpawnerAdapter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TrialSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.spawner.TrialSpawnerConfiguration;

public class TrialSpawnerImplementation implements TrialSpawnerAdapter {

    private static final String SPAWN_EGG_SUFFIX = "_SPAWN_EGG";

    // Marks a placed trial spawner that still owes a cooldown. Changing this makes running
    // servers forget the cooldowns they are currently withholding rewards for
    private static final NamespacedKey LOOT_BLOCKED_UNTIL =
            Objects.requireNonNull(NamespacedKey.fromString("silkspawners:trial_loot_blocked_until"));

    @Override
    public Material getTrialSpawnerMaterial() {
        return Material.TRIAL_SPAWNER;
    }

    @Override
    public boolean isTrialSpawner(Block block) {
        return block != null && block.getType() == Material.TRIAL_SPAWNER;
    }

    @Override
    public TrialSpawnerState readState(Block block) {
        if (!isTrialSpawner(block) || !(block.getState() instanceof TrialSpawner spawner)) return null;
        return new TrialSpawnerState(
                spawner.isOminous(),
                spawner.getCooldownLength(),
                owesCooldown(block, spawner),
                read(spawner.getNormalConfiguration()),
                read(spawner.getOminousConfiguration()));
    }

    @Override
    public void applyState(Block block, TrialSpawnerState state) {
        BlockState blockState = block.getState();
        if (!(blockState instanceof TrialSpawner spawner)) return;

        apply(spawner.getNormalConfiguration(), state.normal());
        apply(spawner.getOminousConfiguration(), state.ominousConfig());
        spawner.setCooldownLength(state.cooldownLength());
        spawner.setOminous(state.ominous());
        applyOminous(blockState, state.ominous());

        blockState.update(true, false);
    }

    // The ominous flag lives in the block entity and in the block data, and the update below
    // rewrites the block data captured before setOminous ran
    private void applyOminous(BlockState blockState, boolean ominous) {
        BlockData blockData = blockState.getBlockData();
        if (!(blockData instanceof org.bukkit.block.data.type.TrialSpawner trialSpawner)) return;
        trialSpawner.setOminous(ominous);
        blockState.setBlockData(trialSpawner);
    }

    @Override
    public void withholdRewards(Block block, int ticks) {
        BlockState blockState = block.getState();
        if (!(blockState instanceof TrialSpawner spawner)) return;

        spawner.getPersistentDataContainer()
                .set(
                        LOOT_BLOCKED_UNTIL,
                        PersistentDataType.LONG,
                        block.getWorld().getGameTime() + ticks);
        blockState.update(true, false);
    }

    @Override
    public EntityType spawnEggEntityType(ItemStack item) {
        if (item == null) return null;
        String material = item.getType().name();
        if (!material.endsWith(SPAWN_EGG_SUFFIX)) return null;
        try {
            return EntityType.valueOf(material.substring(0, material.length() - SPAWN_EGG_SUFFIX.length()));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public void installLootGuard(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new LootGuard(), plugin);
    }

    // The remaining cooldown is not exposed, so the operational phase and a cooldown this plugin
    // is already withholding rewards for are the only signals that a spawner was broken before it
    // was ready again
    private boolean owesCooldown(Block block, TrialSpawner spawner) {
        if (isWithholdingRewards(block, spawner)) return true;
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof org.bukkit.block.data.type.TrialSpawner trialSpawner)) return false;
        return switch (trialSpawner.getTrialSpawnerState()) {
            case WAITING_FOR_REWARD_EJECTION, EJECTING_REWARD, COOLDOWN -> true;
            default -> false;
        };
    }

    private static boolean isWithholdingRewards(Block block, TrialSpawner spawner) {
        Long blockedUntil = spawner.getPersistentDataContainer().get(LOOT_BLOCKED_UNTIL, PersistentDataType.LONG);
        return blockedUntil != null && block.getWorld().getGameTime() < blockedUntil;
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

    private static class LootGuard implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onCall(BlockDispenseLootEvent e) {
            Block block = e.getBlock();
            if (!(block.getState() instanceof TrialSpawner spawner)) return;
            if (!spawner.getPersistentDataContainer().has(LOOT_BLOCKED_UNTIL, PersistentDataType.LONG)) return;

            if (isWithholdingRewards(block, spawner)) {
                e.setCancelled(true);
                return;
            }

            spawner.getPersistentDataContainer().remove(LOOT_BLOCKED_UNTIL);
            spawner.update(true, false);
        }
    }
}
