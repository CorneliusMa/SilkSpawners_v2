package de.corneliusmay.silkspawners.plugin.shop.config;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.shop.SpawnerShop;
import de.corneliusmay.silkspawners.plugin.shop.handler.ShopItem;
import de.corneliusmay.silkspawners.plugin.spawner.Spawner;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.*;

class ShopConfig {

    private final SilkSpawners plugin;

    private final SpawnerShop shop;

    public final FileConfiguration config;

    public ShopConfig(SilkSpawners plugin, SpawnerShop shop, File config) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        this.shop = shop;
        this.config = load(config);
    }

    private FileConfiguration load(File file) throws IOException, InvalidConfigurationException {
        if(!file.exists()) {
            if(!file.createNewFile()) throw new IOException("Error creating shop config file");
            plugin.getLog().warn("Created new shop config with default values. (" + file.getAbsolutePath() + ")");
        }

        FileConfiguration config = new YamlConfiguration();
        config.load(file);
        init(config);
        config.options().copyDefaults(true);
        config.save(file);
        return config;
    }

    private void init(FileConfiguration config) {
        for(String entity : Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).map(EntityType::getName).filter(Objects::nonNull).toList()) {
            config.addDefault(getEntityPath(entity) + "buy", 25000);
            config.addDefault(getEntityPath(entity) + ".sell", 15000);
        }
    }

    private String getEntityPath(String entity) {
        return "entities." + entity + ".";
    }

    public List<ShopItem> getItems() {
        ArrayList<ShopItem> items = new ArrayList<>();

        for(EntityType entity : Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).toArray(EntityType[]::new)) {
            items.add(new ShopItem(plugin, shop.getEconomy(), new Spawner(plugin, entity),
                    config.getInt(getEntityPath(entity.getName()) + "buy"),
                    config.getInt(getEntityPath(entity.getName()) + "sell")
            ));
        }

        return items;
    }
}
