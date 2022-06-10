package de.corneliusmay.silkspawners.plugin.shop.handler;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

public class ShopGUI {

    private final SilkSpawners plugin;

    private final Player player;

    private final List<ShopItem> content;

    public ShopGUI(SilkSpawners plugin, Player player, List<ShopItem> content) {
        this.plugin = plugin;
        this.player = player;
        this.content = content;
    }

    public void open() {
        open(true, 0);
    }

    public void open(boolean buy, int page) {
        player.openInventory(build(buy, player, page));
    }

    private Inventory build(boolean buy, Player player, int page) {
        Inventory inventory = Bukkit.createInventory(null, 45, "§bSpawner Shop §8Page " + (page + 1) + "/" + getPages());
        int modifier = 0;
        for(int i = 0; i < 21; i++) {
            int index = i + page * 21;
            if(content.size() <= index) continue;

            ShopItem spawner = content.get(index);
            String spawnerEntity = new Spawner(plugin, spawner.getItem()).getEntityType().getName();

            boolean canBuy = player.hasPermission("shop.buy." + spawnerEntity);
            boolean canSell = player.hasPermission("shop.sell." + spawnerEntity);
            if(!canBuy && !canSell) {
                i--;
                continue;
            }

            if((i + modifier) % 9 == 0) modifier += 2;

            inventory.setItem(i + modifier + 8, new ItemBuilder(spawner.getItem().clone())
                    .addToLore(canBuy? "§2Buy§7: " + (spawner.canBuy(player)? spawner.getBuyPrice() : "§eNot enough coins") : "")
                    .addToLore(canSell? "§cSell§7: " + (spawner.canSell(player)? spawner.getSellPrice() : "§eSpawner not owned") : "")
                    .build());
        }

        if(page > 0) inventory.setItem(39, new ItemBuilder(UUID.fromString("a68f0b64-8d14-4000-a95f-4b9ba14f8df9")).setDisplayName("§7Previous page").build());
        if((page + 1) < getPages()) inventory.setItem(41, new ItemBuilder(UUID.fromString("50c8510b-5ea0-4d60-be9a-7d542d6cd156")).setDisplayName("§7Next page").build());

        if(buy) inventory.setItem(37, new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§2Buy").build());
        else inventory.setItem(37, new ItemBuilder(Material.RED_WOOL).setDisplayName("§cSell").build());

        inventory.setItem(43, new ItemBuilder(Material.BARRIER).setDisplayName("§cClose").build());

        fill(inventory);
        return inventory;
    }

    private void fill(Inventory inventory) {
        for(int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null) inventory.setItem(i, new ItemBuilder(plugin.getNmsHandler().getPlaceholderMaterial()).setDisplayName("").build());
        }
    }

    private int getPages() {
        return (int) Math.ceil((double) content.size() / 21);
    }
}
