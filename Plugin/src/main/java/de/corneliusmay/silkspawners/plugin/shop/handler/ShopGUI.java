package de.corneliusmay.silkspawners.plugin.shop.handler;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.shop.SpawnerShop;
import de.corneliusmay.silkspawners.plugin.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class ShopGUI {

    private final SilkSpawners plugin;

    private final String shopName;

    private final ExecutorService pool;

    @Getter
    private final List<ShopItem> content;

    @Getter
    private final Player player;

    @Getter
    private int page;

    @Getter
    private boolean modeBuy;

    @Getter
    private Inventory inventory;

    public ShopGUI(SpawnerShop shop, SilkSpawners plugin, Player player, String shopName) {
        this.plugin = plugin;
        this.player = player;
        this.shopName = shopName;
        this.pool = shop.getPool();
        this.content = shop.getConfigHandler().getShopItems(shopName);
        this.modeBuy = true;
        this.page = 0;
    }

    public void open() {
        this.inventory = Bukkit.createInventory(null, 45, getTitle());
        build();
        players.put(player, this);
    }

    public void close(InventoryView inventory) {
        players.remove(player);
        inventory.close();
    }

    public void refresh() {
        build();
    }

    public void setPage(int page) {
        this.page = page;
        build();
    }

    public void setModeBuy(boolean mode) {
        this.modeBuy = mode;
        build();
    }

    public String getTitle() {
        return "§b" + shopName.substring(0, 1).toUpperCase() + shopName.substring(1) + " Shop §8Page " + (page + 1) + "/" + getPages();
    }

    private void build() {
        pool.execute(() -> {
            Inventory inventory = Bukkit.createInventory(null, 45, getTitle());

            setSpawners(inventory);

            if(page > 0) inventory.setItem(39, new ItemBuilder(UUID.fromString("a68f0b64-8d14-4000-a95f-4b9ba14f8df9")).setDisplayName("§7Previous page").build());
            if((page + 1) < getPages()) inventory.setItem(41, new ItemBuilder(UUID.fromString("50c8510b-5ea0-4d60-be9a-7d542d6cd156")).setDisplayName("§7Next page").build());

            if(modeBuy) inventory.setItem(37, new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§2Buy").build());
            else inventory.setItem(37, new ItemBuilder(Material.RED_WOOL).setDisplayName("§cSell").build());

            inventory.setItem(43, new ItemBuilder(Material.BARRIER).setDisplayName("§cClose").build());

            fill(inventory);
            this.inventory = inventory;
            update();
        });
    }

    private void setSpawners(Inventory inventory) {
        int modifier = 0;
        for(int i = 0; i < 21; i++) {
            int index = i + page * 21;
            if(content.size() <= index) continue;

            ShopItem shopItem = content.get(index);
            if((i + modifier) % 9 == 0) modifier += 2;

            inventory.setItem(i + modifier + 8, new ItemBuilder(shopItem.getSpawner().getItemStack().clone())
                    .addToLore("§2Buy§7: " + (shopItem.canBuy(player) ? shopItem.getBuyPrice() : "§eNot enough coins"))
                    .addToLore("§cSell§7: " + (shopItem.canSell(player) ? shopItem.getSellPrice() : "§eSpawner not owned"))
                    .build());
        }
    }

    private void fill(Inventory inventory) {
        for(int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null) inventory.setItem(i, new ItemBuilder(plugin.getNmsHandler().getPlaceholderMaterial()).setDisplayName("§f").build());
        }
    }

    private int getPages() {
        return (int) Math.ceil((double) content.size() / 21);
    }

    private void update() {
        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(this.inventory));
    }

    private final static HashMap<Player, ShopGUI> players;

    static {
        players = new HashMap<>();
    }

    public static ShopGUI getPlayerGUI(Player player) {
        return players.getOrDefault(player, null);
    }
}
