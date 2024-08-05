package de.corneliusmay.silkspawners.nms.v1_9_R2;

import de.corneliusmay.silkspawners.api.NMS;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class NMSHandler implements NMS {

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
