package de.corneliusmay.silkspawners.plugin.explosion;

import de.corneliusmay.silkspawners.api.SpawnerSnapshot;
import de.corneliusmay.silkspawners.api.events.SpawnerExplodeEvent;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Explosion {

    private final List<ExplosionTier> tiers;

    public Explosion(PluginConfig tierList) {
        this.tiers = combined(new ConfigValue<List<ExplosionTier>>(tierList).get(), new ConfigValue<List<ExplosionTier>>(PluginConfig.SPAWNER_EXPLOSION_ALL).get());
    }

    public boolean applies(Player p) {
        return !tiers.isEmpty() && p.hasPermission("silkspawners.explosion");
    }

    public void run(Player player, World world, Location location, SpawnerSnapshot spawner) {
        double total = tiers.stream().mapToDouble(ExplosionTier::chance).sum();
        double roll = ThreadLocalRandom.current().nextDouble(Math.max(total, 100));
        double cumulative = 0;
        for (ExplosionTier tier : tiers) {
            cumulative += tier.chance();
            if (roll >= cumulative) continue;
            if (tier.power() > 0) explode(player, world, location, spawner, tier);
            return;
        }
    }

    private void explode(Player player, World world, Location location, SpawnerSnapshot spawner, ExplosionTier tier) {
        SpawnerExplodeEvent event = new SpawnerExplodeEvent(player, spawner, location, tier.power(), tier.setFire(), tier.breakBlocks());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getPower() <= 0) return;
        world.createExplosion(location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5, event.getPower(), event.getFire(), event.getBreakBlocks());
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
