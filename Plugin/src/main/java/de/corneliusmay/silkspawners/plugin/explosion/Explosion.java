package de.corneliusmay.silkspawners.plugin.explosion;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Explosion {

    public Explosion(Player p, World world, Location location, List<ExplosionTier> tiers) {
        if (tiers.isEmpty() || !p.hasPermission("silkspawners.explosion")) return;
        for (ExplosionTier tier : tiers) {
            if (ThreadLocalRandom.current().nextDouble(100) >= tier.chance()) continue;
            world.createExplosion(location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5, tier.power(), tier.setFire(), tier.breakBlocks());
            return;
        }
    }
}
