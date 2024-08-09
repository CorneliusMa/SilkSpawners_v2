package de.corneliusmay.silkspawners.nms.v1_12_0;

import de.corneliusmay.silkspawners.api.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitHandler implements Bukkit {

    @Override
    public Block getTargetBlock(Player player) {
        return player.getTargetBlock(null, 5);
    }

    @Override
    public ItemStack[] getItemsInHand(Player player) {
        return new ItemStack[]{ player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand() };
    }

    @Override
    public Material getSpawnerMaterial() {
        return Material.MOB_SPAWNER;
    }
}
