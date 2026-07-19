package de.corneliusmay.silkspawners.bukkit.v1_8;

import de.corneliusmay.silkspawners.spi.version.Bukkit;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class BukkitHandler implements Bukkit {

    @Override
    public Block getTargetBlock(Player player) {
        return player.getTargetBlock((Set<Material>) null, 5);
    }

    @Override
    public ItemStack[] getItemsInHand(Player player) {
        return new ItemStack[] {player.getInventory().getItemInHand()};
    }

    @Override
    public Material getSpawnerMaterial() {
        return Material.MOB_SPAWNER;
    }

    @Override
    public ItemFlag getHideAdditionalTooltipFlag() {
        return ItemFlag.HIDE_POTION_EFFECTS;
    }
}
