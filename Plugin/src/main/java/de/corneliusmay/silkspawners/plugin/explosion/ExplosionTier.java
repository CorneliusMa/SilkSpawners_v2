package de.corneliusmay.silkspawners.plugin.explosion;

import java.util.Comparator;

public record ExplosionTier(double chance, float power, boolean setFire, boolean breakBlocks) {
    public static final Comparator<ExplosionTier> STRONGEST_FIRST =
            Comparator.comparingDouble(ExplosionTier::power).reversed();

    public Object chanceValue() {
        int whole = (int) chance;
        return whole == chance ? (Object) whole : chance;
    }

    public Object powerValue() {
        int whole = (int) power;
        return whole == power ? (Object) whole : power;
    }
}
