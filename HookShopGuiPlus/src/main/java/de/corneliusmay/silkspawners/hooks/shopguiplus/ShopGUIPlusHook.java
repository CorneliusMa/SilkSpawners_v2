package de.corneliusmay.silkspawners.hooks.shopguiplus;

import de.corneliusmay.silkspawners.spi.hooks.Hook;
import de.corneliusmay.silkspawners.spi.hooks.SpawnerProvider;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.event.ShopGUIPlusPostEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopGUIPlusHook extends Hook implements Listener {

    public ShopGUIPlusHook(JavaPlugin plugin, SpawnerProvider spawnerProvider) {
        super(plugin, spawnerProvider);
    }

    @Override
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onShopGuiPlusPostEnable(ShopGUIPlusPostEnableEvent event) {
        try {
            ShopGuiPlusApi.registerSpawnerProvider(new ShopGuiPlusSpawner(plugin.getName(), spawnerProvider));
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register spawner provider in ShopGUI+: " + e.getMessage());
        }
    }
}
