package de.corneliusmay.silkspawners.plugin.shop.handler;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class ShopItem {

    private final SilkSpawners plugin;

    private final Economy economy;

    @Getter
    private final Spawner spawner;

    @Getter
    private final Integer buyPrice;

    @Getter
    private final Integer sellPrice;

    public ShopItem(SilkSpawners plugin, Economy economy, Spawner spawner, Integer buyPrice, Integer sellPrice) {
        this.plugin = plugin;
        this.economy = economy;
        this.spawner = spawner;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;

        if(!spawner.isValid()) throw new RuntimeException("The spawner " + spawner.getEntityType().getName() +  " is invalid");
    }

    public boolean canBuy(Player player) {
        if(buyPrice < 0) return false;
        return !(economy.getBalance(player) < buyPrice);
    }

    public boolean canSell(Player player) {
        return sellPrice >= 0 && getSpawnerFromInventory(player) != null;
    }

    public boolean buy(Player player) {
        if(!canBuy(player)) return false;

        player.getInventory().addItem(spawner.getItemStack());
        economy.withdrawPlayer(player, buyPrice);
        return true;
    }

    public boolean sell(Player player) {
        if(!canSell(player)) return false;
        Spawner inventorySpawner = getSpawnerFromInventory(player);
        player.getInventory().removeItem(inventorySpawner.getItemStack());

        economy.depositPlayer(player, sellPrice);
        return true;
    }

    private Spawner getSpawnerFromInventory(Player player) {
        for(Spawner inventorySpawner : Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).map(itemStack -> {
                    ItemStack newStack = itemStack.clone();
                    newStack.setAmount(1);
                    return newStack;
        }).map((itemStack) -> new Spawner(plugin, itemStack)).filter(Spawner::isValid).toList()) {
            if(this.spawner.getEntityType() == inventorySpawner.getEntityType()) return inventorySpawner;
        }
        return null;
    }
}
