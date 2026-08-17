package de.corneliusmay.silkspawners.plugin.spawner.policy;

import de.corneliusmay.silkspawners.spi.version.VersionAdapter;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SilkDropCheck {

    private final VersionAdapter versionAdapter;

    private final SpawnerTypeProfile profile;

    public boolean canSilkDrop(Player player, String entity) {
        return profile.canBreak(player, entity) && hasSilkTouchTool(player);
    }

    private boolean hasSilkTouchTool(Player player) {
        return Arrays.stream(versionAdapter.getItemsInHand(player)).anyMatch(this::isSilkTouchTool);
    }

    private boolean isSilkTouchTool(ItemStack item) {
        return isRequiredTool(item) && hasRequiredSilkTouchLevel(item);
    }

    private boolean isRequiredTool(ItemStack item) {
        return versionAdapter.isPickaxe(item) || !profile.pickaxeRequired().get();
    }

    private boolean hasRequiredSilkTouchLevel(ItemStack item) {
        return item.getEnchantmentLevel(Enchantment.SILK_TOUCH)
                        >= profile.silktouchLevel().get()
                || !profile.silktouchRequired().get();
    }
}
