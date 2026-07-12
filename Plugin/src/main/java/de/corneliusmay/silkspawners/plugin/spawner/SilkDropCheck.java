package de.corneliusmay.silkspawners.plugin.spawner;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SilkDropCheck {

    private final SilkSpawners plugin;

    public SilkDropCheck(SilkSpawners plugin) {
        this.plugin = plugin;
    }

    public boolean canSilkDrop(Player player, Spawner spawner) {
        return hasBreakPermission(player, spawner) && hasSilkTouchTool(player);
    }

    private boolean hasBreakPermission(Player player, Spawner spawner) {
        return player.hasPermission("silkspawners.break." + spawner.serializedEntityType())
                || player.hasPermission("silkspawners.break.*")
                || new ConfigValue<Boolean>(PluginConfig.SPAWNER_PERMISSION_DISABLE_DESTROY).get();
    }

    private boolean hasSilkTouchTool(Player player) {
        return Arrays.stream(plugin.getBukkitHandler().getItemsInHand(player)).anyMatch(this::isSilkTouchTool);
    }

    private boolean isSilkTouchTool(ItemStack item) {
        return isRequiredTool(item) && hasRequiredSilkTouchLevel(item);
    }

    private boolean isRequiredTool(ItemStack item) {
        return item.getType().toString().contains("PICKAXE")
                || !new ConfigValue<Boolean>(PluginConfig.SPAWNER_PICKAXE_REQUIRED).get();
    }

    private boolean hasRequiredSilkTouchLevel(ItemStack item) {
        return item.getEnchantmentLevel(Enchantment.SILK_TOUCH) >= new ConfigValue<Integer>(PluginConfig.SPAWNER_SILKTOUCH_LEVEL).get()
                || !new ConfigValue<Boolean>(PluginConfig.SPAWNER_SILKTOUCH_REQUIRED).get();
    }
}
