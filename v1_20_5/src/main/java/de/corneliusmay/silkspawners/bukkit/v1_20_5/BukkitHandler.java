package de.corneliusmay.silkspawners.bukkit.v1_20_5;

import de.corneliusmay.silkspawners.api.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class BukkitHandler implements Bukkit {

    @Override
    public Block getTargetBlock(Player player) {
        double range = 5;

        AttributeInstance blockRange = player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE);
        if (blockRange != null) {
            range = blockRange.getValue();
        }

        RayTraceResult hitResult = player.rayTraceBlocks(range);
        return hitResult != null ? hitResult.getHitBlock() : null;
    }

    @Override
    public ItemStack[] getItemsInHand(Player player) {
        return new ItemStack[]{player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand()};
    }

    @Override
    public Material getSpawnerMaterial() {
        return Material.SPAWNER;
    }
}
