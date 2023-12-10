package de.corneliusmay.silkspawners.nms.v1_20_R3;

import de.corneliusmay.silkspawners.api.NMS;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NMSHandler implements NMS {

    @Override
    public Block getTargetBlock(Player player) {
        return player.getTargetBlockExact(5);
    }

    @Override
    public ItemStack[] getItemsInHand(Player player) {
        return new ItemStack[]{ player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand() };
    }

    @Override
    public Material getSpawnerMaterial() {
        return Material.SPAWNER;
    }

    @Override
    public Material getPlaceholderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }
}
