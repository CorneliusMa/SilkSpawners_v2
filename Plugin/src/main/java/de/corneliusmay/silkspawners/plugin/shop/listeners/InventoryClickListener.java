package de.corneliusmay.silkspawners.plugin.shop.listeners;

import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopGUI;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopItem;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener extends SilkSpawnersListener<InventoryClickEvent> {

    @Override @EventHandler
    protected void onCall(InventoryClickEvent event) {
        ShopGUI gui = ShopGUI.getPlayerGUI((Player) event.getView().getPlayer());
        if(gui == null || event.getRawSlot() > 44) return;
        event.setCancelled(true);

        ItemStack clickedItem = gui.getInventory().getItem(event.getSlot());
        Spawner spawner = new Spawner(plugin, clickedItem);
        if(spawner.isValid()) onSpawnerClick(gui, spawner);
        else switch (clickedItem.getItemMeta().getDisplayName()) {
            case "§2Buy", "§cSell" -> gui.setModeBuy(!gui.isModeBuy());
            case "§7Next page" -> gui.setPage(gui.getPage() + 1);
            case "§7Previous page" -> gui.setPage(gui.getPage() - 1);
            case "§cClose" -> gui.close(event.getView());
        }
    }

    private void onSpawnerClick(ShopGUI gui, Spawner spawner) {
        ShopItem shopItem = gui.getContent().stream().filter((item) -> item.getSpawner().getEntityType() == spawner.getEntityType()).findFirst().orElse(null);
        if (shopItem == null) return;

        Player player = gui.getPlayer();
        if(gui.isModeBuy()) {
            if(shopItem.buy(player)) player.sendMessage(plugin.getLocale().getMessage("SHOP_SUCCESS_BUY", shopItem.getSpawner().serializedName()));
            else player.sendMessage(plugin.getLocale().getMessage("SHOP_ERROR_BUY"));
        } else {
            if(shopItem.sell(player)) player.sendMessage(plugin.getLocale().getMessage("SHOP_SUCCESS_SELL", shopItem.getSpawner().serializedName()));
            else player.sendMessage(plugin.getLocale().getMessage("SHOP_ERROR_SELL", shopItem.getSpawner().serializedName()));
        }
        gui.refresh();
    }
}
