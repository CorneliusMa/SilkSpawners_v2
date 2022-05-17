package de.corneliusmay.silkspawners.plugin.utils;

import org.bukkit.Location;
import org.bukkit.World;

public class Explosion {

    public Explosion(World world, Location location, int intensity) {
        if(intensity < 1) return;
        world.createExplosion(location, intensity);
    }
}
