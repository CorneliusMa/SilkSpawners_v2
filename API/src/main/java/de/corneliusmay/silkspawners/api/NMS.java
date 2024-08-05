package de.corneliusmay.silkspawners.api;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMS {

    Block getTargetBlock(Player player);

    ItemStack[] getItemsInHand(Player player);

    Material getSpawnerMaterial();

}
