package de.silkspawners.bukkit.v1_9_4;

import de.corneliusmay.silkspawners.api.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class BukkitHandler implements Bukkit {

    @Override
    public Block getTargetBlock(Player player) {
        return player.getTargetBlock((Set<Material>) null, 5);
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
