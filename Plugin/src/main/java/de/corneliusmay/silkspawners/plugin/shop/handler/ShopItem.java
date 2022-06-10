package de.corneliusmay.silkspawners.plugin.shop.handler;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class ShopItem {

    private final Economy economy;

    @Getter
    private final ItemStack item;

    @Getter
    private final Integer buyPrice;

    @Getter
    private final Integer sellPrice;

    public ShopItem(Economy economy, ItemStack item, Integer buyPrice, Integer sellPrice) {
        this.economy = economy;
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public boolean canBuy(Player player) {
        if(buyPrice < 0) return false;
        return !(economy.getBalance(player) < buyPrice);
    }

    public boolean canSell(Player player) {
        if(sellPrice < 0) return false;

        for(String item : Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull)
                .map((itemStack -> itemStack.getItemMeta().getLore())).filter(Objects::nonNull).filter((lore) -> lore.size() > 0).map((lore) -> lore.get(0)).toList()) {
            if(this.item.getItemMeta().getLore().get(0).equals(item)) return true;
        }

        return false;
    }

    public boolean buy(Player player) {
        if(!canBuy(player)) return false;

        player.getInventory().addItem(item);
        economy.withdrawPlayer(player, buyPrice);
        return true;
    }

    public boolean sell(Player player) {
        if(!canSell(player)) return false;

        for(ItemStack itemStack :  Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull)
                .filter((item) -> (item.getItemMeta() != null && item.getItemMeta().getLore() != null)).filter((item) -> item.getItemMeta().getLore().size() > 0).toList()) {
            if(this.item.getItemMeta().getLore().get(0).equals(itemStack.getItemMeta().getLore().get(0))) {
                player.getInventory().removeItem(itemStack);
                break;
            }
        }

        economy.depositPlayer(player, sellPrice);
        return true;
    }
}
