package de.corneliusmay.silkspawners.plugin.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Explosion {

    public Explosion(Player p, World world, Location location, int intensity) {
        if(!p.hasPermission("silkspawners.explosion") || intensity < 1) return;
        world.createExplosion(location, intensity);
    }
}
