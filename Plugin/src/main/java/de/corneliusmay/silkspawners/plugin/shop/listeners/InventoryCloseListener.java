package de.corneliusmay.silkspawners.plugin.shop.listeners;

import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersListener;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

public class InventoryCloseListener extends SilkSpawnersListener<InventoryCloseEvent> {

    @Override @EventHandler
    protected void onCall(InventoryCloseEvent event) {
        InventoryView view = event.getView();
        ShopGUI gui = ShopGUI.getPlayerGUI((Player) view.getPlayer());
        if(gui == null) return;
        String title = gui.getTitle();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            String viewTitle = view.getPlayer().getOpenInventory().getTitle();
            if(!viewTitle.substring(0, (viewTitle.length() - 3)).equals(title.substring(0, (title.length() - 3)))) gui.close(view);
        }, 1);
    }
}
