package de.corneliusmay.silkspawners.plugin.shop.config;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueArray;
import de.corneliusmay.silkspawners.plugin.shop.SpawnerShop;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopItem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHandler {

    private final SilkSpawners plugin;

    private final SpawnerShop shop;

    private final File dataPath;

    private final Map<String, List<ShopItem>> cache;

    public ConfigHandler(SilkSpawners plugin, SpawnerShop shop) {
        this.plugin = plugin;
        this.shop = shop;
        this.dataPath = new File(plugin.getDataFolder() + "/shop");
        dataPath.mkdirs();
        this.cache = new HashMap<>();

        for(String config : new ConfigValueArray<String>(PluginConfig.SHOP_CONFIG).get()) {
            loadConfig(config);
        }
    }

    private void loadConfig(String config) {
        File configFile = new File(dataPath.getAbsolutePath() + "/" + config + "-shop.yml");
        try {
            List<ShopItem> items = new ShopConfig(plugin, shop, configFile).getItems();
            cache.put(config, items);
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.getLog().error("Error loading shop config", ex);
        }
    }

    public String getShop(Player player, String shop) {
        return getShops(player).stream().filter((name) -> name.equalsIgnoreCase(shop)).findFirst().orElse(null);
    }

    public List<String> getShops(Player player) {
        return new ConfigValueArray<String>(PluginConfig.SHOP_CONFIG).get().stream().map(String::toLowerCase).filter((name) -> player.hasPermission("spawnershop.use." + name)).toList();
    }

    public List<ShopItem> getShopItems(String config) {
        return cache.getOrDefault(config, null);
    }
}
