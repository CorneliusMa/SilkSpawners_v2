package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
public class SilkDropCheck {

    private final VersionAdapter versionAdapter;

    public boolean canSilkDrop(Player player, Spawner spawner) {
        return hasBreakPermission(player, spawner) && hasSilkTouchTool(player);
    }

    private boolean hasBreakPermission(Player player, Spawner spawner) {
        return player.hasPermission("silkspawners.break." + spawner.serializedEntityType())
                || player.hasPermission("silkspawners.break.*")
                || PluginConfig.SPAWNER_PERMISSION_DISABLE_DESTROY.get();
    }

    private boolean hasSilkTouchTool(Player player) {
        return Arrays.stream(versionAdapter.getItemsInHand(player)).anyMatch(this::isSilkTouchTool);
    }

    private boolean isSilkTouchTool(ItemStack item) {
        return isRequiredTool(item) && hasRequiredSilkTouchLevel(item);
    }

    private boolean isRequiredTool(ItemStack item) {
        return versionAdapter.isPickaxe(item) || !PluginConfig.SPAWNER_PICKAXE_REQUIRED.get();
    }

    private boolean hasRequiredSilkTouchLevel(ItemStack item) {
        return item.getEnchantmentLevel(Enchantment.SILK_TOUCH) >= PluginConfig.SPAWNER_SILKTOUCH_LEVEL.get()
                || !PluginConfig.SPAWNER_SILKTOUCH_REQUIRED.get();
    }
}
