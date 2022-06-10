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
        String inventoryTitle = inventoryView.getTitle();
        if (!inventoryTitle.startsWith("§bSpawner Shop §8Page ") || event.getRawSlot() > 44 || event.getCurrentItem() == null) return;

        event.setCancelled(true);

        boolean buy = event.getInventory().getItem(37).getItemMeta().getDisplayName().equals("§2Buy");

        String actionName = event.getCurrentItem().getItemMeta().getDisplayName();
        final String spawnerName = new ConfigValue<String>(PluginConfig.SPAWNER_ITEM_NAME).get();

        if (actionName.equals(spawnerName)) {
            Spawner spawner = new Spawner(plugin, event.getCurrentItem());
            if (!spawner.isValid()) return;

            ShopItem shopItem = spawnerShop.getConfigHandler().getShopItems(new ConfigValue<String>(PluginConfig.SHOP_CONFIG).get()).stream().filter((item) -> {
                Spawner configSpawner = item.getSpawner();
                if (!configSpawner.isValid()) return false;

                return configSpawner.getEntityType() == spawner.getEntityType();
            }).toList().stream().findFirst().orElse(null);
            if (shopItem == null) return;

            if(buy) {
                if(shopItem.buy(player)) {
                    player.sendMessage("Successfully bought 1 spawner.");
                    update(player, true, inventoryTitle);
                }
                else player.sendMessage("You can't buy this spawner.");
            } else {
                if(shopItem.sell(player)) {
                    player.sendMessage("Successfully sold 1 spawner.");
                    update(player, false, inventoryTitle);
                }
                else player.sendMessage("You can't sell this spawner.");
            }

        } else {
            switch (actionName) {
                case "§2Buy", "§cSell" -> {
                    buy = !buy;
                    update(player, buy, inventoryTitle);
                }
                case "§7Next page" -> new ShopGUI(plugin, player, spawnerShop.getConfigHandler().getShopItems(new ConfigValue<String>(PluginConfig.SHOP_CONFIG).get()))
                        .open(buy, Integer.parseInt(inventoryTitle.replace("§bSpawner Shop §8Page ", "").split("/")[0]));
                case "§7Previous page" -> new ShopGUI(plugin, player, spawnerShop.getConfigHandler().getShopItems(new ConfigValue<String>(PluginConfig.SHOP_CONFIG).get()))
                        .open(buy, Integer.parseInt(inventoryTitle.replace("§bSpawner Shop §8Page ", "").split("/")[0]) - 2);
                case "§cClose" -> inventoryView.close();
            }
        }
    }

    private void update(Player player, boolean buy, String inventoryTitle) {
        new ShopGUI(plugin, player, spawnerShop.getConfigHandler().getShopItems(new ConfigValue<String>(PluginConfig.SHOP_CONFIG).get()))
                .open(buy, Integer.parseInt(inventoryTitle.replace("§bSpawner Shop §8Page ", "").split("/")[0]) - 1);
    }
}
