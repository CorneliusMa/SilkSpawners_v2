package de.corneliusmay.silkspawners.spi.version;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public interface Bukkit {

    Block getTargetBlock(Player player);

    ItemStack[] getItemsInHand(Player player);

    Material getSpawnerMaterial();

    ItemFlag getHideAdditionalTooltipFlag();

    boolean isPickaxe(ItemStack item);
}
