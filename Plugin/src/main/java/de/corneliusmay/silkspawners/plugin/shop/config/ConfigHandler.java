package de.corneliusmay.silkspawners.plugin.shop.config;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.shop.SpawnerShop;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopItem;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    }

    public List<ShopItem> getShopItems(String config) {
        if(cache.containsKey(config)) return cache.get(config);

        File configFile = new File(dataPath.getAbsolutePath() + "/" + config + "-shop.yml");
        try {
            List<ShopItem> items = new ShopConfig(plugin, shop, configFile).getItems();
            cache.put(config, items);
            return items;
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.getLog().error("Error loading shop config", ex);
            return new ArrayList<>();
        }
    }
}
