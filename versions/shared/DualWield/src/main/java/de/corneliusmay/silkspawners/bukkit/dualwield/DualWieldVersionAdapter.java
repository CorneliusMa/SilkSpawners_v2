package de.corneliusmay.silkspawners.bukkit.dualwield;

import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface DualWieldVersionAdapter extends VersionAdapter {

    @Override
    default ItemStack[] getItemsInHand(Player player) {
        return new ItemStack[] {
            player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand()
        };
    }
}
