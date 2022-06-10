package de.corneliusmay.silkspawners.plugin.shop.listeners;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.shop.SpawnerShop;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopGUI;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopItem;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

public class InventoryClickListener extends SilkSpawnersListener<InventoryClickEvent> {

    private final SpawnerShop spawnerShop;

    public InventoryClickListener(SpawnerShop spawnerShop) {
        this.spawnerShop = spawnerShop;
    }

    @Override @EventHandler
    protected void onCall(InventoryClickEvent event) {
        InventoryView inventoryView = event.getView();
        Player player = (Player) inventoryView.getPlayer();
        String shopName = getShopName(inventoryView.getTitle(), player);
        int page = getPage(shopName, inventoryView.getTitle());
        if (shopName == null || event.getRawSlot() > 44 || event.getCurrentItem() == null || page == -1) return;
        event.setCancelled(true);

        boolean buy = event.getInventory().getItem(37).getItemMeta().getDisplayName().equals("§2Buy");

        String actionName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (actionName.equals(new ConfigValue<String>(PluginConfig.SPAWNER_ITEM_NAME).get())) {
            Spawner spawner = new Spawner(plugin, event.getCurrentItem());
            if (!spawner.isValid()) return;

            ShopItem shopItem = spawnerShop.getConfigHandler().getShopItems(shopName).stream().filter((item) -> {
                Spawner configSpawner = item.getSpawner();
                if (!configSpawner.isValid()) return false;

                return configSpawner.getEntityType() == spawner.getEntityType();
            }).toList().stream().findFirst().orElse(null);

            if (shopItem == null) return;

            if(buy) {
                if(shopItem.buy(player)) player.sendMessage(plugin.getLocale().getMessage("SHOP_SUCCESS_BUY", shopItem.getSpawner().serializedName()));
                else player.sendMessage(plugin.getLocale().getMessage("SHOP_ERROR_BUY"));
            } else {
                if(shopItem.sell(player)) player.sendMessage(plugin.getLocale().getMessage("SHOP_SUCCESS_SELL", shopItem.getSpawner().serializedName()));
                else player.sendMessage(plugin.getLocale().getMessage("SHOP_ERROR_SELL", shopItem.getSpawner().serializedName()));
            }
            new ShopGUI(spawnerShop, plugin, player, shopName).open(buy, page);
        } else {
            switch (actionName) {
                case "§2Buy", "§cSell" -> {
                    buy = !buy;
                    new ShopGUI(spawnerShop, plugin, player, shopName).open(buy, page);
                }
                case "§7Next page" -> new ShopGUI(spawnerShop, plugin, player, shopName).open(buy, page + 1);
                case "§7Previous page" -> new ShopGUI(spawnerShop, plugin, player, shopName).open(buy, page - 1);
                case "§cClose" -> inventoryView.close();
            }
        }
    }

    private String getShopName(String inventoryTitle, Player player) {
        for(String shop : spawnerShop.getConfigHandler().getShops(player)) {
            if(inventoryTitle.toLowerCase().contains(shop)) return shop;
        }

        return null;
    }

    private int getPage(String shopName, String inventoryTitle) {
        try {
            return Integer.parseInt(inventoryTitle.replace("§b" + shopName.substring(0, 1).toUpperCase() + shopName.substring(1) + " Shop §8Page ", "").split("/")[0]) - 1;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
