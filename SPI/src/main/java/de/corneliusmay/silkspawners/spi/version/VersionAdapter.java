package de.corneliusmay.silkspawners.spi.version;

import de.corneliusmay.silkspawners.api.SpawnerSettings;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public interface VersionAdapter {

    Block getTargetBlock(Player player);

    ItemStack[] getItemsInHand(Player player);

    Material getSpawnerMaterial();

    ItemFlag getHideAdditionalTooltipFlag();

    boolean isPickaxe(ItemStack item);

    ItemStack writeTag(ItemStack itemStack, String tag, String value);

    Map<String, String> readTags(ItemStack itemStack, String... tags);

    SpawnerSettings readSpawnerSettings(CreatureSpawner spawner);

    void applySpawnerSettings(CreatureSpawner spawner, SpawnerSettings settings);
}
