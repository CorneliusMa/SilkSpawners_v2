package de.corneliusmay.silkspawners.plugin.explosion;

import java.util.Comparator;

public record ExplosionTier(double chance, float power, boolean setFire, boolean breakBlocks) {
    public static final Comparator<ExplosionTier> STRONGEST_FIRST =
            Comparator.comparingDouble(ExplosionTier::power).reversed();
}
