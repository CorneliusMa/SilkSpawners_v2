package de.corneliusmay.silkspawners.plugin.explosion;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Explosion {

    public Explosion(Player p, World world, Location location, List<ExplosionTier> tiers) {
        List<ExplosionTier> sharedTiers = new ConfigValue<List<ExplosionTier>>(PluginConfig.SPAWNER_EXPLOSION_ALL).get();
        if ((tiers.isEmpty() && sharedTiers.isEmpty()) || !p.hasPermission("silkspawners.explosion")) return;
        for (ExplosionTier tier : combined(tiers, sharedTiers)) {
            if (ThreadLocalRandom.current().nextDouble(100) >= tier.chance()) continue;
            world.createExplosion(location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5, tier.power(), tier.setFire(), tier.breakBlocks());
            return;
        }
    }

    private List<ExplosionTier> combined(List<ExplosionTier> tiers, List<ExplosionTier> sharedTiers) {
        if (sharedTiers.isEmpty()) return tiers;
        if (tiers.isEmpty()) return sharedTiers;
        List<ExplosionTier> combined = new ArrayList<>(tiers);
        combined.addAll(sharedTiers);
        combined.sort(ExplosionTier.STRONGEST_FIRST);
        return combined;
    }
}
