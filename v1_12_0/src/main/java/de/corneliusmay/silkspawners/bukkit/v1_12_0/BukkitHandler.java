package de.corneliusmay.silkspawners.bukkit.v1_12_0;

import de.corneliusmay.silkspawners.spi.version.Bukkit;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class BukkitHandler implements Bukkit {

    private static final Set<Material> PICKAXES = EnumSet.of(
            Material.WOOD_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLD_PICKAXE,
            Material.DIAMOND_PICKAXE);

    @Override
    public Block getTargetBlock(Player player) {
        return player.getTargetBlock(null, 5);
    }

    @Override
    public ItemStack[] getItemsInHand(Player player) {
        return new ItemStack[] {
            player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand()
        };
    }

    @Override
    public Material getSpawnerMaterial() {
        return Material.MOB_SPAWNER;
    }

    @Override
    public ItemFlag getHideAdditionalTooltipFlag() {
        return ItemFlag.HIDE_POTION_EFFECTS;
    }

    @Override
    public boolean isPickaxe(ItemStack item) {
        return PICKAXES.contains(item.getType());
    }
}
