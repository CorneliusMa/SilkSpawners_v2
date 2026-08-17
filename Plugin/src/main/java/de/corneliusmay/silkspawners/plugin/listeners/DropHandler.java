package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
class DropHandler {

    private final ServerPlatform platform;

    boolean missesDropChance(double chance) {
        return ThreadLocalRandom.current().nextDouble() * 100 > chance;
    }

    void drop(BlockBreakEvent e, ItemStack item, long delay) {
        e.setExpToDrop(0);
        Location location = e.getBlock().getLocation();
        if (delay > 0) platform.runTaskLater(location, () -> dropItem(location, item), delay);
        else dropItem(location, item);
    }

    private void dropItem(Location location, ItemStack item) {
        location.getWorld().dropItemNaturally(location, item);
    }
}
