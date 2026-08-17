package de.corneliusmay.silkspawners.plugin.spawner.trial;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.entity.EntityNames;
import de.corneliusmay.silkspawners.plugin.entity.StoredEntityNames;
import de.corneliusmay.silkspawners.plugin.spawner.EditedSpawners;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.spi.version.TrialSpawnerAdapter;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor
public class TrialSpawnerFactory implements Loader {

    private static final String ENTITY_TAG = "silkspawners:trial_entity";

    private static final String STATE_TAG = "silkspawners:trial_state";

    private final PluginConfig config;

    private final VersionAdapter versionAdapter;

    private final TrialSpawnerAdapter trialSpawnerAdapter;

    private final ServerPlatform platform;

    private final EditedSpawners editedSpawners;

    private final Logger logger;

    @Override
    public boolean load() {
        if (!isSupported() && config.TRIAL_SPAWNER_ENABLED.get())
            logger.warn("trialspawner.enabled is set, but this server version has no trial spawner support"
                    + " (requires Minecraft 1.21.3 or newer)");
        return true;
    }

    private boolean isSupported() {
        return material() != null;
    }

    public boolean isEnabled() {
        return isSupported() && config.TRIAL_SPAWNER_ENABLED.get();
    }

    public boolean isTrialSpawner(Block block) {
        return trialSpawnerAdapter.isTrialSpawner(block);
    }

    private boolean isTrialSpawnerItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == material();
    }

    public EntityType spawnEggEntityType(ItemStack itemStack) {
        return isEnabled() ? trialSpawnerAdapter.spawnEggEntityType(itemStack) : null;
    }

    public Optional<TrialSpawner> fromBlock(Block block) {
        if (!isEnabled() || !trialSpawnerAdapter.isTrialSpawner(block)) return Optional.empty();
        return of(trialSpawnerAdapter.readState(block));
    }

    public Optional<TrialSpawner> fromItem(ItemStack itemStack) {
        if (!isEnabled() || !isTrialSpawnerItem(itemStack)) return Optional.empty();
        Map<String, String> tags = versionAdapter.readTags(itemStack.clone(), ENTITY_TAG, STATE_TAG);
        String entityName = tags.get(ENTITY_TAG);
        if (entityName == null) return Optional.empty();

        EntityType entityType = StoredEntityNames.resolve(entityName);
        if (entityType == null && !entityName.equals(EntityNames.EMPTY)) {
            logger.warn("Ignoring trial spawner item with unrecognized entity '" + entityName
                    + "' (not supported on this server version)");
            return Optional.empty();
        }

        TrialSpawnerState state = TrialSpawnerStateFormat.deserialize(tags.get(STATE_TAG));
        return of(state != null ? state : TrialSpawnerStateFormat.defaultState(entityType));
    }

    public Optional<TrialSpawner> ofType(EntityType entityType) {
        return of(TrialSpawnerStateFormat.defaultState(entityType));
    }

    public Optional<TrialSpawner> of(TrialSpawnerState state) {
        if (!isEnabled() || state == null) return Optional.empty();
        ItemStack itemStack = new ItemBuilder(material())
                .setDisplayName(itemName(state))
                .addToLore(itemLore(state))
                .addItemFlags(versionAdapter.getHideAdditionalTooltipFlag())
                .writeTag(versionAdapter, ENTITY_TAG, EntityNames.serialized(TrialSpawner.entityType(state)))
                .writeTag(versionAdapter, STATE_TAG, TrialSpawnerStateFormat.serialize(state))
                .build();

        TrialSpawner trialSpawner = new TrialSpawner(state, itemStack);
        return trialSpawner.isValid() ? Optional.of(trialSpawner) : Optional.empty();
    }

    public void applyToBlock(TrialSpawnerState state, Block block) {
        applyToBlock(state, block, () -> {});
    }

    public void applyToBlock(TrialSpawnerState state, Block block, Runnable onApplied) {
        apply(state, block, false, onApplied);
    }

    public void placeAtBlock(TrialSpawnerState state, Block block) {
        apply(state, block, true, () -> {});
    }

    private void apply(TrialSpawnerState state, Block block, boolean placing, Runnable onApplied) {
        if (!isEnabled()) {
            editedSpawners.endEdit(block.getLocation());
            return;
        }
        boolean withholdRewards = placing && state.cooldownPending() && config.TRIAL_SPAWNER_CARRY_COOLDOWN.get();
        platform.runTaskLater(
                block.getLocation(),
                () -> {
                    try {
                        if (!trialSpawnerAdapter.isTrialSpawner(block)) return;
                        trialSpawnerAdapter.applyState(block, state);
                        if (withholdRewards) trialSpawnerAdapter.withholdRewards(block, state.cooldownLength());
                        onApplied.run();
                    } finally {
                        editedSpawners.endEdit(block.getLocation());
                    }
                },
                1);
    }

    private Material material() {
        return trialSpawnerAdapter.getTrialSpawnerMaterial();
    }

    private String itemName(TrialSpawnerState state) {
        String name =
                state.ominous() ? config.TRIAL_SPAWNER_ITEM_OMINOUS_NAME.get() : config.TRIAL_SPAWNER_ITEM_NAME.get();
        return name.replace("{entity}", EntityNames.displayName(TrialSpawner.entityType(state)));
    }

    private List<String> itemLore(TrialSpawnerState state) {
        EntityType entityType = TrialSpawner.entityType(state);
        String entityName = entityType == null ? "Nothing" : EntityNames.displayName(entityType);
        return config.TRIAL_SPAWNER_ITEM_LORE.get().stream()
                .map(line -> line.replace("{entity}", entityName))
                .toList();
    }
}
