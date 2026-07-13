package de.corneliusmay.silkspawners.plugin.spawner;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.entity.EntityType;

public final class SpawnableEntities {

    public static final Set<EntityType> TYPES = Collections.unmodifiableSet(Arrays.stream(EntityType.values())
            .filter(EntityType::isSpawnable)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(EntityType.class))));

    private SpawnableEntities() {}
}
