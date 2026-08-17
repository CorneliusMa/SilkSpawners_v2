package de.corneliusmay.silkspawners.plugin.spawner.trial;

import de.corneliusmay.silkspawners.api.TrialSpawnerState;
import de.corneliusmay.silkspawners.plugin.entity.EntityNames;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class TrialSpawner {

    @Getter
    private final TrialSpawnerState state;

    private final ItemStack itemStack;

    TrialSpawner(TrialSpawnerState state, ItemStack itemStack) {
        this.state = state;
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack == null ? null : itemStack.clone();
    }

    public EntityType getEntityType() {
        return entityType(state);
    }

    public String serializedEntityType() {
        return EntityNames.serialized(getEntityType());
    }

    public boolean isEmpty() {
        return getEntityType() == null;
    }

    boolean isValid() {
        EntityType entityType = getEntityType();
        return itemStack != null && (entityType == null || entityType.isSpawnable());
    }

    public static EntityType entityType(TrialSpawnerState state) {
        EntityType active = state.active().entityType();
        return active != null ? active : state.normal().entityType();
    }
}
