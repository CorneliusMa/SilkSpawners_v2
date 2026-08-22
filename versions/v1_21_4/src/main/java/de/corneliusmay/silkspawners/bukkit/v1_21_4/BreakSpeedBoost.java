package de.corneliusmay.silkspawners.bukkit.v1_21_4;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.DoubleSupplier;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class BreakSpeedBoost implements Listener {

    private static final NamespacedKey BREAK_SPEED_KEY =
            Objects.requireNonNull(NamespacedKey.fromString("silkspawners:trial_break_speed"));

    private final BiPredicate<Player, Block> eligible;

    private final DoubleSupplier multiplier;

    BreakSpeedBoost(BiPredicate<Player, Block> eligible, DoubleSupplier multiplier) {
        this.eligible = eligible;
        this.multiplier = multiplier;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(BlockDamageEvent e) {
        double factor = multiplier.getAsDouble();
        boolean boost = !e.isCancelled() && factor != 1 && eligible.test(e.getPlayer(), e.getBlock());
        if (boost) apply(e.getPlayer(), factor);
        else reset(e.getPlayer());
    }

    @EventHandler
    public void onAbort(BlockDamageAbortEvent e) {
        reset(e.getPlayer());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        reset(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        reset(e.getPlayer());
    }

    private void apply(Player player, double factor) {
        reset(player);
        AttributeInstance attribute = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attribute == null) return;
        attribute.addTransientModifier(
                new AttributeModifier(BREAK_SPEED_KEY, factor - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    }

    private void reset(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attribute == null) return;
        for (AttributeModifier modifier : List.copyOf(attribute.getModifiers())) {
            if (modifier.getKey().equals(BREAK_SPEED_KEY)) attribute.removeModifier(modifier);
        }
    }
}
