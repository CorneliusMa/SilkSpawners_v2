package de.corneliusmay.silkspawners.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMS {

    ItemStack[] getItemsInHand(Player player);

    Material getSpawnerMaterial();

}
